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

public class DocumentedModelController<M extends DocumentedModel & Model> extends VerifiableController<M>{
    public DocumentedModelController(Path path) {
        super(path);
    }

}
