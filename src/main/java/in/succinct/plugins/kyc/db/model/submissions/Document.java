package in.succinct.plugins.kyc.db.model.submissions;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.kyc.db.model.submissions.SubmittedDocument;

import java.util.List;

public interface Document extends Model {

    @UNIQUE_KEY
    @Index
    public String getDocumentedModelName();
    public void setDocumentedModelName(String documentedModelName);

    @UNIQUE_KEY
    public String getDocumentName();
    public void setDocumentName(String documentName);


    public Long getKycGroupId();
    public void setKycGroupId(Long id);
    public KycGroup getKycGroup();

    public List<SubmittedDocument> getSubmittedDocuments();

}
