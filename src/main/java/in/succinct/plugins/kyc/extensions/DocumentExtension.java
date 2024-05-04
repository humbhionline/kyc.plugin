package in.succinct.plugins.kyc.extensions;

import com.venky.swf.db.extensions.ModelOperationExtension;
import in.succinct.plugins.kyc.db.model.submissions.Document;
import in.succinct.plugins.kyc.extensions.SubmittedDocumentExtension.KycInspector;

public class DocumentExtension extends ModelOperationExtension<Document> {
    static {
        registerExtension(new DocumentExtension());
    }
    @Override
    public void beforeValidate(Document model) {
        if (model.getRawRecord().isFieldDirty("REQUIRED_FOR_KYC")) {
            KycInspector.submitDocumentInspection(model);
        }
    }

    @Override
    protected void beforeDestroy(Document instance) {
        KycInspector.submitDocumentInspection(instance);
    }

}
