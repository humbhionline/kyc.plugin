package in.succinct.plugins.kyc.db.model;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.model.Model;

public interface Verifiable  {
    public static final String APPROVED="Approved";
    public static final String REJECTED="Rejected";
    public static final String PENDING="Pending";
    public static final String BEING_REVIEWED="Being Reviewed";

    @Enumeration(PENDING+"," +BEING_REVIEWED +"," +APPROVED+","+REJECTED)
    @PROTECTION(Kind.NON_EDITABLE)
    @Index
    @COLUMN_DEF(value = StandardDefault.SOME_VALUE , args = PENDING)
    public String getVerificationStatus();
    public void setVerificationStatus(String status);

    public static final String BEING_VERIFIED= "being.verified";

    public void approve();
    public void reject();
    public void approve(boolean persist);
    public void reject(boolean persist);
    public void submit();

    public String getRemarks();
    public void setRemarks(String remarks);

}
