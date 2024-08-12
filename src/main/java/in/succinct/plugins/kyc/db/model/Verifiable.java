package in.succinct.plugins.kyc.db.model;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
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

    @Enumeration(APPROVED+","+REJECTED+","+PENDING)
    @PROTECTION(Kind.NON_EDITABLE)
    @COLUMN_DEF(value = StandardDefault.SOME_VALUE,args = PENDING)
    @Index
    public String getVerificationStatus();
    public void setVerificationStatus(String status);

    public static final String BEING_VERIFIED= "being.verified";

}
