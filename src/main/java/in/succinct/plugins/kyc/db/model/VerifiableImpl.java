package in.succinct.plugins.kyc.db.model;

import com.venky.swf.db.model.Model;
import com.venky.swf.db.table.ModelImpl;

import java.sql.Timestamp;

public class VerifiableImpl<M extends Model & Verifiable> extends ModelImpl<M> {
    public VerifiableImpl(M m){
        super(m);
    }

    public void approve(){
        approve(true);
    }
    public void reject(){
        reject(true);
    }
    public void approve(boolean persist){
        M m = getProxy();
        approve(m);
        if (persist) {
            m.save();
        }
    }
    public void reject(boolean persist){
        M m = getProxy();
        reject(m);
        if (persist) {
            m.save();
        }
    }

    public void approve(M m){
        m.setTxnProperty(Verifiable.BEING_VERIFIED,true);
        m.setVerificationStatus(VerifiableDocument.APPROVED);
        m.setRemarks(null);
    }
    public void reject(M m){
        m.setTxnProperty(Verifiable.BEING_VERIFIED,true);
        m.setVerificationStatus(VerifiableDocument.REJECTED);
    }

    public void submit(){
        M m = getProxy();
        m.setVerificationStatus(Verifiable.BEING_REVIEWED);
        m.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        m.save();
    }

    public void revokeApproval(){
        M m = getProxy();
        m.setVerificationStatus(Verifiable.PENDING);
        m.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        m.save();
    }
}
