package will;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Example {

    public void example(Player player) {
        final EasyBoard easyboard = new EasyBoard(player, "Titulo") {
            // Coloque tudo que voce quer atualizar aqui!!
            @Override
            public void update() {
                setText(new String[] {
                        "Texto 1", // Linha 3
                        "Texto 2", // Linha 2
                        "Texto 3" // Linha 3
                });
            }
        };

        new BukkitRunnable() {
            @Override
            public void run() {
                easyboard.update();
            }
        }.runTaskTimer(classePrincipal, 0L, 0L); // Deixe em 0L, 0L para atualizar muito rapido!
    }

}
