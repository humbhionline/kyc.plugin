package in.succinct.plugins.kyc.db.model;

import com.venky.core.util.Bucket;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.model.Model;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.kyc.db.model.submissions.KycGroup;
import in.succinct.plugins.kyc.db.model.submissions.SubmittedDocument;
import in.succinct.plugins.kyc.extensions.SubmittedDocumentExtension.KycInspector;

import java.util.List;

public class DocumentedModelProxy<M extends Model & DocumentedModel> implements DocumentedModel{
    M model;
    VerifiableImpl<M> verifiableProxy ;

    public DocumentedModelProxy(M m){
        model = m;
        this.verifiableProxy = new VerifiableImpl<>(m);
    }



    @Override
    public List<SubmittedDocument> getSubmittedDocuments() {
        Select select  =new Select().from(SubmittedDocument.class);
        return  select.where(new Expression(select.getPool(), Conjunction.AND).
                add(new Expression(select.getPool(),"DOCUMENTED_MODEL_ID", Operator.EQ, model.getId())).
                add(new Expression(select.getPool(),"DOCUMENTED_MODEL_NAME",Operator.EQ,model.getReflector().getModelClass().getSimpleName()))).execute();
    }

    @Override
    public Integer getMinDocumentsNeeded(KycGroup group){
        return group.getMinDocumentsNeeded();
    }

    @Override
    public void approve() {
        List<SubmittedDocument> submittedDocumentList =  model.getSubmittedDocuments();
        Bucket numDocuments = new Bucket();
        if (!submittedDocumentList.isEmpty()){
            for (SubmittedDocument submittedDocument : submittedDocumentList) {
                if (!ObjectUtil.equals(submittedDocument.getVerificationStatus(), Verifiable.APPROVED)){
                    submittedDocument.approve();
                    numDocuments.increment();
                }
            }
        }
        if (numDocuments.intValue() == 0) {
            KycInspector.submitModelInspection(model);
        }
        //verifiableProxy.approve(persist);
    }

    @Override
    public void reject() {
        verifiableProxy.reject();
    }

    @Override
    public void submit() {
        verifiableProxy.submit();
    }

    @Override
    public void revokeApproval() {
        verifiableProxy.revokeApproval();
    }


    @Override
    public String getVerificationStatus() {
        return model.getVerificationStatus();
    }

    @Override
    public void setVerificationStatus(String status) {
        model.setVerificationStatus(status);
    }
    @Override
    public String getRemarks() {
        return model.getRemarks();
    }

    @Override
    public void setRemarks(String remarks) {
        model.setRemarks(remarks);
    }



}
