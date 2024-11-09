package in.succinct.plugins.kyc.extensions;

import com.venky.core.util.Bucket;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.ModelOperationExtension;
import com.venky.swf.db.model.Model;
import com.venky.swf.exceptions.AccessDeniedException;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.kyc.db.model.DocumentedModel;
import in.succinct.plugins.kyc.db.model.Verifiable;
import in.succinct.plugins.kyc.db.model.submissions.Document;
import in.succinct.plugins.kyc.db.model.submissions.SubmittedDocument;
import in.succinct.plugins.kyc.extensions.SubmittedDocumentExtension.KycInspector;
import in.succinct.plugins.kyc.util.DocumentedModelRegistry;

import java.util.List;

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


}
