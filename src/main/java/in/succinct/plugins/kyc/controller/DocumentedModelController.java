package in.succinct.plugins.kyc.controller;

import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.db.model.Model;
import com.venky.swf.path.Path;
import com.venky.swf.views.View;
import in.succinct.plugins.kyc.db.model.DocumentedModel;
import in.succinct.plugins.kyc.db.model.Verifiable;
import in.succinct.plugins.kyc.db.model.submissions.SubmittedDocument;
import in.succinct.plugins.kyc.extensions.SubmittedDocumentExtension.KycInspector;

import java.util.List;

public class DocumentedModelController<M extends DocumentedModel & Model & Verifiable> extends VerifiableController<M>{
    public DocumentedModelController(Path path) {
        super(path);
    }
    @SingleRecordAction(icon = "fas fa-check", tooltip = "Mark Approved")
    public View approve(long id){
        M m = Database.getTable(getModelClass()).get(id);
        List<SubmittedDocument> submittedDocumentList =  m.getSubmittedDocuments();
        if (!submittedDocumentList.isEmpty()){
            for (SubmittedDocument submittedDocument : submittedDocumentList) {
                submittedDocument.approve();
            }
        }else {
            KycInspector.submitModelInspection(m);
        }

        if (getIntegrationAdaptor() == null){
            return back();
        }else {
            return show(m);
        }
    }

    @SingleRecordAction(icon = "fas fa-times", tooltip = "Mark Rejected")
    public View reject(long id){
        return super.reject(id);
    }
}
