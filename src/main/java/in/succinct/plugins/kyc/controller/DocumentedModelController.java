package in.succinct.plugins.kyc.controller;

import com.venky.core.util.Bucket;
import com.venky.core.util.ObjectUtil;
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
        Bucket numDocumentsApproved = new Bucket();

        if (!submittedDocumentList.isEmpty()){
            for (SubmittedDocument submittedDocument : submittedDocumentList) {
                if (!ObjectUtil.equals(submittedDocument.getVerificationStatus(),Verifiable.APPROVED)){
                    submittedDocument.approve();
                    numDocumentsApproved.increment();
                }
            }
        }
        if (numDocumentsApproved.intValue() == 0){
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
        M m = Database.getTable(getModelClass()).get(id);
        if (ObjectUtil.equals(getPath().getRequest().getMethod(),"POST")){
            List<M> ms = getIntegrationAdaptor().readRequest(getPath());
            if (!ms.isEmpty()){
                m.setRemarks(ms.get(0).getRemarks());
            }
        }

        m.setKycComplete(false);
        m.reject();

        if (getIntegrationAdaptor() == null){
            return back();
        }else {
            return show(m);
        }
    }

    @SingleRecordAction(icon = "fas fa-cloud-upload-alt", tooltip = "Submit for review")
    public View submit(long id){
        M document = Database.getTable(getModelClass()).get(id);
        document.setKycComplete(false);
        document.submit();
        if (getIntegrationAdaptor() == null){
            return back();
        }else {
            return show(document);
        }
    }
}
