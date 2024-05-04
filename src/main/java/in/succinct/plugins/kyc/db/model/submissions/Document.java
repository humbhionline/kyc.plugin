package in.succinct.plugins.kyc.db.model.submissions;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.kyc.db.model.submissions.SubmittedDocument;

import java.util.List;

public interface Document extends Model {

    @UNIQUE_KEY
    public String getDocumentedModelName();
    public void setDocumentedModelName(String documentedModelName);

    @UNIQUE_KEY
    public String getDocumentName();
    public void setDocumentName(String documentName);

    public boolean isRequiredForKyc();
    public void setRequiredForKyc(boolean requiredForKyc);


    public List<SubmittedDocument> getSubmittedDocuments();

}
