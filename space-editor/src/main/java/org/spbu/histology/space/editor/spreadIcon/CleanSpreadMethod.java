package org.spbu.histology.space.editor.spreadIcon;
import org.openide.util.Lookup;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.space.editor.HomeController;

public class CleanSpreadMethod extends HomeController {
    public void clean(){
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        hm.getAllHistions().forEach(h -> {
            if (h.getId() != 0) {
                h.getItems().forEach(c -> {
                    hm.getHistionMap().get(h.getId()).deleteChild(c.getId());
                });
                hm.deleteHistion(h.getId());
            }
        });
        disableEverything.set(false);
    }
}