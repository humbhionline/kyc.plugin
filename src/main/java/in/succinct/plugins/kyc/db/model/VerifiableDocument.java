package in.succinct.plugins.kyc.db.model;

import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.PASSWORD;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;

import java.io.InputStream;
import java.sql.Date;

public interface VerifiableDocument  extends Verifiable {



    @IS_VIRTUAL
    public boolean isExpired();

    @UNIQUE_KEY(allowMultipleRecordsWithNull = false)
    public Date getValidFrom();
    public void setValidFrom(Date validFrom);

    @UNIQUE_KEY(allowMultipleRecordsWithNull = false)
    public Date getValidTo();
    public void setValidTo(Date validTo);

    public InputStream getFile();
    public void setFile(InputStream is);

    @PASSWORD
    public String getPassword();
    public void setPassword(String password);

    @PROTECTION(Kind.NON_EDITABLE)
    public String getFileContentName();
    public void setFileContentName(String name);

    @HIDDEN
    @PROTECTION(Kind.NON_EDITABLE)
    public String getFileContentType();
    public void setFileContentType(String contentType);

    @HIDDEN
    @PROTECTION(Kind.NON_EDITABLE)
    public int getFileContentSize();
    public void setFileContentSize(int size);


    @IS_VIRTUAL
    @PROTECTION(Kind.NON_EDITABLE)
    public String getImageUrl();
    public void setImageUrl(String imageUrl);

    public String getRemarks();
    public void setRemarks(String remarks);

}
