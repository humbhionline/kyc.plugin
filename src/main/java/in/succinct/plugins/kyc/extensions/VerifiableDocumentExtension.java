package in.succinct.plugins.kyc.extensions;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.ModelOperationExtension;
import com.venky.swf.db.model.Model;
import com.venky.swf.exceptions.AccessDeniedException;
import in.succinct.plugins.kyc.db.model.VerifiableDocument;

public class VerifiableDocumentExtension<M extends VerifiableDocument & Model> extends VerifiableExtension<M> {
    @Override
    public void beforeValidate(M document) {
        super.beforeValidate(document);
        if (document.getRawRecord().isFieldDirty("FILE") ){
            try {
                if (document.getFile() != null) {
                    document.setFileContentSize(document.getFile().available());
                }else {
                    document.setFileContentSize(0);
                }
            }catch (Exception ex){
                //
            }
        }
    }
}
