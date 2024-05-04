package in.succinct.plugins.kyc.extensions;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.ModelOperationExtension;
import com.venky.swf.db.model.Model;
import com.venky.swf.exceptions.AccessDeniedException;
import in.succinct.plugins.kyc.db.model.Verifiable;

public class VerifiableExtension<M extends Model & Verifiable> extends ModelOperationExtension<M> {
    @Override
    protected void beforeValidate(M instance) {
        if (!ObjectUtil.equals(true,instance.getTxnProperty("being.verified"))){
            if (instance.getRawRecord().isFieldDirty("VERIFICATION_STATUS") && !instance.getVerificationStatus().equals(Verifiable.PENDING)) {
                throw new AccessDeniedException();
            }else if (instance.isDirty()){
                instance.setVerificationStatus(Verifiable.PENDING);
            }
        }


    }
}
