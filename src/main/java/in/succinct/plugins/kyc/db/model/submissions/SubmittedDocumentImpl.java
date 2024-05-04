package in.succinct.plugins.kyc.db.model.submissions;

import com.venky.swf.db.Database;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.kyc.db.model.DocumentedModel;
import in.succinct.plugins.kyc.db.model.VerifiableDocumentImpl;
import in.succinct.plugins.kyc.util.DocumentedModelRegistry;

public class SubmittedDocumentImpl extends VerifiableDocumentImpl<SubmittedDocument> {
    public SubmittedDocumentImpl(SubmittedDocument p){
        super(p);
    }

    public <R extends Model & DocumentedModel> R extractDocumentedModel() {
        Class<R> clazz = DocumentedModelRegistry.getInstance().getDocumentedModelClass(getProxy().getDocumentedModelName());
        if (clazz == null){
            return null;
        }
        try {
            return  Database.getTable(clazz).get(getProxy().getDocumentedModelId());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
