package in.succinct.plugins.kyc.db.model;

import com.venky.core.string.StringUtil;
import com.venky.swf.db.model.Model;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.routing.Config;

public class VerifiableDocumentImpl<M extends Model & VerifiableDocument> extends VerifiableImpl<M> {
    public VerifiableDocumentImpl(M p){
        super(p);
    }

    public boolean isExpired(){
        M p = getProxy();
        if (p.getValidFrom() != null && p.getValidTo() != null){
            if (System.currentTimeMillis() > p.getValidTo().getTime()){
                return true;
            }
        }
        return false;
    }

    public String getImageUrl() {
        if (getProxy().getFileContentSize() > 0) {
            return Config.instance().getServerBaseUrl() + "/"+
                    StringUtil.pluralize(getReflector().getTableName().toLowerCase())+"/view/" + getProxy().getId()     ;
        }else {
            return imageUrl;
        }
    }
    String imageUrl;
    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
}
