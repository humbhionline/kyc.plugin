package in.succinct.plugins.kyc.db.model;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.COLUMN_SIZE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.relationship.CONNECTED_VIA;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.kyc.db.model.submissions.Document;
import in.succinct.plugins.kyc.db.model.submissions.KycGroup;
import in.succinct.plugins.kyc.db.model.submissions.SubmittedDocument;

import java.util.List;

public interface DocumentedModel extends Verifiable {


    @IS_VIRTUAL
    Integer getMinDocumentsNeeded(KycGroup group);



    @IS_VIRTUAL
    public List<SubmittedDocument> getSubmittedDocuments();
}
