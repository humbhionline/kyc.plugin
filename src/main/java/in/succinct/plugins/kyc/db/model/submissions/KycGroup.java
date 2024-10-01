package in.succinct.plugins.kyc.db.model.submissions;

import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;

import java.util.List;

public interface KycGroup extends Model {
    @UNIQUE_KEY
    public String getName();
    public void setName(String name);

    static KycGroup find(String name){
        KycGroup kycGroup = Database.getTable(KycGroup.class).newRecord();
        kycGroup.setName(name);
        kycGroup = Database.getTable(KycGroup.class).find(kycGroup,false);
        return kycGroup;
    }

    @IS_NULLABLE
    public Integer getMinDocumentsNeeded();
    public void setMinDocumentsNeeded(Integer minDocumentsNeeded);
    //Null means all in the group are  needed.


    public List<Document> getDocuments();
}
