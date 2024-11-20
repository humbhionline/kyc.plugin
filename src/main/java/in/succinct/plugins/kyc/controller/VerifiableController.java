package in.succinct.plugins.kyc.controller;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.controller.ModelController;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.db.model.Model;
import com.venky.swf.path.Path;
import com.venky.swf.views.View;
import in.succinct.plugins.kyc.db.model.Verifiable;
import in.succinct.plugins.kyc.db.model.VerifiableDocument;

import java.util.List;

public class VerifiableController<M extends Verifiable & Model> extends ModelController<M> {
    public VerifiableController(Path path) {
        super(path);
    }

    @SingleRecordAction(icon = "fas fa-check", tooltip = "Mark Approved")
    public View approve(long id){
        M document = Database.getTable(getModelClass()).get(id);
        document.approve();
        if (getIntegrationAdaptor() == null){
            return back();
        }else {
            return show(document);
        }
    }

    @SingleRecordAction(icon = "fas fa-times", tooltip = "Mark Rejected")
    public View reject(long id){
        M document = Database.getTable(getModelClass()).get(id);
        if (ObjectUtil.equals(getPath().getRequest().getMethod(),"POST")){
            List<M> ms = getIntegrationAdaptor().readRequest(getPath());
            if (!ms.isEmpty()){
                document.setRemarks(ms.get(0).getRemarks());
            }
        }

        document.reject();
        if (getIntegrationAdaptor() == null){
            return back();
        }else {
            return show(document);
        }
    }

    @SingleRecordAction(icon = "fas fa-cloud-upload-alt", tooltip = "Submit for review")
    public View submit(long id){
        M document = Database.getTable(getModelClass()).get(id);
        document.submit();
        if (getIntegrationAdaptor() == null){
            return back();
        }else {
            return show(document);
        }
    }
    
    @SingleRecordAction(icon = "fas fa-cloud-upload-alt", tooltip = "Submit for review")
    public View revokeApproval(long id){
        M document = Database.getTable(getModelClass()).get(id);
        document.revokeApproval();
        if (getIntegrationAdaptor() == null){
            return back();
        }else {
            return show(document);
        }
    }

}
