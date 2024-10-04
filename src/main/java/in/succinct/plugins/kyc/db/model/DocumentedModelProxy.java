package in.succinct.plugins.kyc.db.model;

import com.venky.swf.db.model.Model;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.kyc.db.model.submissions.Document;
import in.succinct.plugins.kyc.db.model.submissions.KycGroup;
import in.succinct.plugins.kyc.db.model.submissions.SubmittedDocument;

import java.util.List;

public class DocumentedModelProxy<M extends Model & DocumentedModel> implements DocumentedModel{
    M model;
    public DocumentedModelProxy(M m){
        model = m;
    }

/*    public List<Document> getDocuments(){
        Select select  =new Select().from(Document.class);
        return  select.where(new Expression(select.getPool(), Conjunction.AND).
                add(new Expression(select.getPool(),"DOCUMENTED_MODEL_NAME", Operator.EQ, model.getReflector().getModelClass().getSimpleName()))).execute();
    }
*/
    public boolean isKycComplete(){
        return model.isKycComplete();
    }

    @Override
    public void setKycComplete(boolean kycComplete) {
        model.setKycComplete(kycComplete);
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
}
