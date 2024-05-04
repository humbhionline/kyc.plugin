package in.succinct.plugins.kyc.db.model.submissions;

import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.kyc.db.model.DocumentedModel;
import in.succinct.plugins.kyc.db.model.VerifiableDocument;

public interface SubmittedDocument extends Model , VerifiableDocument {
    @UNIQUE_KEY
    public Long getDocumentedModelId();
    public void setDocumentedModelId(Long id);

    @UNIQUE_KEY
    public String getDocumentedModelName();
    public void setDocumentedModelName(String documentModelName);

    @IS_VIRTUAL
    @HIDDEN
    public <R extends Model & DocumentedModel> R extractDocumentedModel();



    @UNIQUE_KEY
    public Long getDocumentId();
    public void setDocumentId(Long id);
    public Document getDocument();


}
