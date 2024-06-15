package in.succinct.plugins.kyc.extensions;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.ModelOperationExtension;
import com.venky.swf.db.model.Model;
import com.venky.swf.exceptions.AccessDeniedException;
import in.succinct.plugins.kyc.db.model.Verifiable;

public class VerifiableExtension<M extends Model & Verifiable> extends ModelOperationExtension<M> {
    public boolean isChangeAllowedAfterApproval(){
        return true;
    }
    @Override
    protected void beforeValidate(M instance) {
        if (!ObjectUtil.equals(true,instance.getTxnProperty("being.verified"))){
            if (instance.getRawRecord().isFieldDirty("VERIFICATION_STATUS") && !instance.getVerificationStatus().equals(Verifiable.PENDING)) {
                if (Database.getInstance().getCurrentUser() != null && Database.getInstance().getCurrentUser().getId() > 1) {
                    throw new AccessDeniedException();
                }
            }else if (instance.isDirty()){
                if (ObjectUtil.equals(instance.getVerificationStatus(),Verifiable.APPROVED)) {
                    if (isChangeAllowedAfterApproval()) {
                        instance.setVerificationStatus(Verifiable.PENDING);
                    }else {
                        throw new AccessDeniedException("Cannot modify after approval! Please create a fresh request for approval.");
                    }
                }else {
                    instance.setVerificationStatus(Verifiable.PENDING);
                }
            }
        }


    }
}
