package in.succinct.plugins.kyc.extensions;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.kyc.db.model.DocumentedModel;
import in.succinct.plugins.kyc.db.model.VerifiableDocument;
import in.succinct.plugins.kyc.db.model.submissions.Document;
import in.succinct.plugins.kyc.db.model.submissions.SubmittedDocument;
import in.succinct.plugins.kyc.util.DocumentedModelRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmittedDocumentExtension extends VerifiableDocumentExtension<SubmittedDocument> {
    static {
        registerExtension(new SubmittedDocumentExtension());
    }

    @Override
    public void beforeValidate(SubmittedDocument submittedDocument) {
        if (submittedDocument.getDocument() == null){
            return;
        }
        super.beforeValidate(submittedDocument);
    }

    @Override
    public void afterSave(SubmittedDocument model) {
        KycInspector.submitModelInspection(model.extractDocumentedModel());
    }
    @Override
    public void afterDestroy(SubmittedDocument model) {
        KycInspector.submitModelInspection(model.extractDocumentedModel());
    }

    public static class KycInspector<R extends Model & DocumentedModel>  implements Task {
        R model;
        public KycInspector(R model){
            this.model = model;
        }
        static <R extends Model & DocumentedModel> void submitDocumentInspection(Document model){
            Class<R> clazz = DocumentedModelRegistry.getInstance().getDocumentedModelClass(model.getDocumentedModelName());
            submitModelInspections(new Select().from(clazz).execute());
        }
        private static <R extends Model & DocumentedModel> void submitModelInspection(R kycModel){
            TaskManager.instance().executeAsync(new KycInspector<R>(kycModel),false); //Should be true.
        }
        private static <R extends Model & DocumentedModel> void submitModelInspections(List<R> models) {
            for (R model : models) {
                submitModelInspection(model);
            }
        }



        @Override
        public int hashCode() {
            return (model.getClass().getName() + ":" + model.getId()).hashCode();
        }

        @Override
        public void execute() {
            Select select = new Select().from(Document.class);
            select.where(new Expression(select.getPool(), Conjunction.AND).
                    add(new Expression(select.getPool(),"REQUIRED_FOR_KYC", Operator.EQ,true)).
                    add(new Expression(select.getPool(),"DOCUMENTED_MODEL_NAME", Operator.EQ,model.getReflector().getModelClass().getSimpleName())));
            List<Document> documents = select.execute();

            List<SubmittedDocument> submittedDocuments = model.getSubmittedDocuments();
            Map<Long,Boolean> kycRequirementCompletionMap = new HashMap<>();
            documents.forEach(p->{
                kycRequirementCompletionMap.put(p.getId(),false);
            });
            submittedDocuments.forEach(sd->{
                if (kycRequirementCompletionMap.containsKey(sd.getDocumentId())){
                    if (!sd.isExpired() && ObjectUtil.equals(sd.getVerificationStatus(), VerifiableDocument.APPROVED)){
                        kycRequirementCompletionMap.remove(sd.getDocumentId());
                    }
                }
            });
            if (kycRequirementCompletionMap.isEmpty()){
                model.setTxnProperty("kyc.complete",true);
                model.setKycComplete(true);
                model.save();
            }else {
                model.setKycComplete(false);
                model.save();
            }
        }
    }
}
