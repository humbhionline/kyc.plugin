package in.succinct.plugins.kyc.controller;

import com.venky.swf.path.Path;
import in.succinct.plugins.kyc.db.model.submissions.SubmittedDocument;

public class SubmittedDocumentsController extends VerifiableController<SubmittedDocument> {
    public SubmittedDocumentsController(Path path) {
        super(path);
    }
}
