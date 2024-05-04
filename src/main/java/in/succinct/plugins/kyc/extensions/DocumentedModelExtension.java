package in.succinct.plugins.kyc.extensions;

import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.ModelOperationExtension;
import com.venky.swf.db.model.Model;
import com.venky.swf.exceptions.AccessDeniedException;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.kyc.db.model.DocumentedModel;
import in.succinct.plugins.kyc.db.model.submissions.Document;
import in.succinct.plugins.kyc.util.DocumentedModelRegistry;

public class DocumentedModelExtension<R extends Model & DocumentedModel> extends ModelOperationExtension<R> {
    static {
        for (String modelName : DocumentedModelRegistry.getInstance().documentModelNames()){
            registerExtension(new DocumentedModelExtension<>(DocumentedModelRegistry.getInstance().getDocumentedModelClass(modelName)));
        }
    }
    Class<R> modelClass ;
    DocumentedModelExtension(Class<R> modelClass){
        this.modelClass = modelClass;
    }

    @Override
    protected Class<R> getModelClass() {
        return modelClass;
    }

    @Override
    public void beforeValidate(R model) {
        if (model.isKycComplete() && model.getRawRecord().isFieldDirty("KYC_COMPLETE")){
            if (!model.getReflector().getJdbcTypeHelper().getTypeRef(boolean.class).getTypeConverter().valueOf(model.getTxnProperty("kyc.complete"))){
                throw new AccessDeniedException();
            }
        }
        Select select = new Select().from(Document.class);
        select.where(new Expression(select.getPool(),"DOCUMENTED_MODEL_NAME", Operator.EQ,getModelClass().getSimpleName()));
        if (select.execute(1).isEmpty()){
            model.setKycComplete(true);
        }
    }
}
