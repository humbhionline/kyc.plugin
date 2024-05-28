package in.succinct.plugins.kyc.db.model.submissions;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;

public interface KycGroup extends Model {
    @UNIQUE_KEY
    public String getName();
    public void setName(String name);

    @IS_NULLABLE
    public Integer getMinDocumentsNeeded();
    public void setMinDocumentsNeeded(Integer minDocumentsNeeded);
    //Null means all in the group are  needed.
}
