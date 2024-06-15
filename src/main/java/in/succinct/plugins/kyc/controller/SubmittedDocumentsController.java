package in.succinct.plugins.kyc.controller;

import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.path.Path;
import com.venky.swf.views.View;
import in.succinct.plugins.kyc.db.model.submissions.SubmittedDocument;

public class SubmittedDocumentsController extends VerifiableController<SubmittedDocument> {
    public SubmittedDocumentsController(Path path) {
        super(path);
    }

    @RequireLogin(value = false)
    public View view(long id) {
        return super.view(id);
    }

}
