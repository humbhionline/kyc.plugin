package in.succinct.plugins.kyc.extensions;

import com.venky.cache.UnboundedCache;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.kyc.db.model.DocumentedModel;
import in.succinct.plugins.kyc.db.model.Verifiable;
import in.succinct.plugins.kyc.db.model.VerifiableDocument;
import in.succinct.plugins.kyc.db.model.submissions.Document;
import in.succinct.plugins.kyc.db.model.submissions.KycGroup;
import in.succinct.plugins.kyc.db.model.submissions.SubmittedDocument;
import in.succinct.plugins.kyc.util.DocumentedModelRegistry;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        public static <R extends Model & DocumentedModel> void submitModelInspection(R kycModel){
            TaskManager.instance().executeAsync(new KycInspector<>(kycModel),false); //Should be true.
        }
        private static <R extends Model & DocumentedModel> void submitModelInspections(List<R> models) {
            for (R model : models) {
                submitModelInspection(model);
            }
        }



        @Override
        public int hashCode() {
            if (model != null) {
                return (model.getClass().getName() + ":" + model.getId()).hashCode();
            }else {
                return super.hashCode();
            }
        }


        public Map<Long, Document> getDocumentMap(){
            Select select = new Select().from(Document.class);
            select.where(new Expression(select.getPool(), Conjunction.AND).
                    add(new Expression(select.getPool(),"KYC_GROUP_ID", Operator.NE)).
                    add(new Expression(select.getPool(),"DOCUMENTED_MODEL_NAME", Operator.EQ,model.getReflector().getModelClass().getSimpleName())));
            List<Document> documents = select.execute();
            Map<Long,Document> documentMap = new UnboundedCache<Long, Document>() {
                @Override
                protected Document getValue(Long key) {
                    return null;
                }
            };
            documents.forEach(d->documentMap.put(d.getId(),d));
            return documentMap;
        }
        public Map<Long,KycGroup> getKycGroupMap(){
            Map<Long,KycGroup> kycGroupMap = new UnboundedCache<Long, KycGroup>() {
                @Override
                protected KycGroup getValue(Long key) {
                    return null;
                }
            };
            for (KycGroup kycGroup : new Select().from(KycGroup.class).execute(KycGroup.class)) {
                kycGroupMap.put(kycGroup.getId(),kycGroup);
            }
            return kycGroupMap;
        }


        @Override
        public void execute() {
            Map<Long,Document> documentMap = getDocumentMap();
            Map<Long,KycGroup> kycGroupMap = getKycGroupMap();
            Map<Long, Set<Long>> kycRequirementMap = getKycRequirementMap(documentMap);
            Map<Long, Set<Long>> kycSubmittedDocumentsMap  = getKycSubmissionMap(documentMap);
            Set<Long> kycGroupsToInspect = new HashSet<>(kycRequirementMap.keySet());


            kycGroupsToInspect.forEach(groupId->{
                KycGroup group = kycGroupMap.get(groupId);
                Set<Long> requiredDocumentIds = kycRequirementMap.get(groupId);
                Set<Long> submittedDocumentIds = kycSubmittedDocumentsMap.get(groupId);
                Integer minDocsNeeded = getMinDocumentsNeeded(group,model);
                if (minDocsNeeded != null){
                    if (kycSubmittedDocumentsMap.get(groupId).size() >= minDocsNeeded){
                        kycRequirementMap.remove(groupId); //Not required to submit the rest.
                    }
                }else {
                    requiredDocumentIds.removeAll(submittedDocumentIds);
                    if (requiredDocumentIds.isEmpty()){
                        kycRequirementMap.remove(groupId); // Group requirement is met.
                    }
                }
            });



            if (kycRequirementMap.isEmpty() ){
                if (!model.isKycComplete()) {
                    model.setTxnProperty("kyc.complete", true);
                    model.setKycComplete(true);
                }
                model.setUpdatedAt(new Timestamp(System.currentTimeMillis()));//Force and update. to ensure before validate get called.!! Bad idea but doeasnot seem to have much choice.
                model.save();
            }else if (model.isKycComplete()){
                model.setKycComplete(false);
                model.setUpdatedAt(new Timestamp(System.currentTimeMillis()));//Force and update. to ensure before validate get called.!! Bad idea but doeasnot seem to have much choice.
                model.save();
            }else{
                StringBuilder message = new StringBuilder();
                kycRequirementMap.forEach((groupId,docSet)->{
                    KycGroup group = kycGroupMap.get(groupId);
                    int minDocuments = getMinDocumentsNeeded(group,model);
                    message.append(String.format("At least %d %s needed for %s \n",
                            minDocuments,
                            (minDocuments == 1 ? "Document" : "Documents"),
                            group.getName()));
                });
                if (model instanceof Verifiable) {
                    Verifiable v = (Verifiable)model;
                    if (!ObjectUtil.equals(v.getVerificationStatus(),Verifiable.PENDING)) {
                        v.setRemarks(message.toString());
                    }
                }
                model.save();
            }
        }

        private Integer getMinDocumentsNeeded(KycGroup group, R model) {
            return model.getMinDocumentsNeeded(group);
        }

        @NotNull
        private Map<Long, Set<Long>> getKycRequirementMap(Map<Long, Document> documentMap) {
            return new UnboundedCache<Long, Set<Long>>() {
                {
                    documentMap.forEach((id, document)-> get(document.getKycGroupId()).add(id));
                }
                @Override
                protected Set<Long> getValue(Long key) {
                    return new HashSet<>();
                }
            };
        }
        @NotNull
        private Map<Long, Set<Long>> getKycSubmissionMap(Map<Long,Document> documentMap) {
            return new UnboundedCache<Long, Set<Long>>() {
                {
                    model.getSubmittedDocuments().forEach(sd -> {
                        Document document = documentMap.get(sd.getDocumentId());
                        if (document != null && document.getKycGroupId() != null){
                            if (!sd.isExpired() && ObjectUtil.equals(sd.getVerificationStatus(), VerifiableDocument.APPROVED)){
                                get(document.getKycGroupId()).add(document.getId());
                            }
                        }
                    });
                }
                @Override
                protected Set<Long> getValue(Long key) {
                    return new HashSet<>();
                }
            };
        }
    }
}
