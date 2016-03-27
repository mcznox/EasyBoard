package znox;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public abstract class EasyBoard {

    Player player;
    Scoreboard scoreboard;
    Objective objective;
    Map<Player, EasyBoard> scoreboards = Maps.newHashMap();

    public EasyBoard(Player player, String display) {
        this.player = player;
        scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective(player.getName(), "dummy");
        objective.setDisplayName(display);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboards.put(player, this);
        player.setScoreboard(scoreboard);
    }

    /**
     * Obter objective da scoreboard
     * @return objective
     */
    public Objective getObjective() { return objective; }

    /**
     * Obter scoreboard
     * @return scoreboard
     */
    public Scoreboard getScoreboard() { return scoreboard; }

    /**
     * Obter jogador
     * @return player
     */
    public Player getPlayer() { return player; }

    /**
     * Obter scoreboards
     * @return map das scoreboards
     */
    public Map<Player, EasyBoard> getScoreboards() { return scoreboards; }

    /**
     * Setar um novo display name pra scoreboard
     * @param display novo titulo
     */
    public void setDisplayName(String display) {
        getObjective().setDisplayName(display);
    }

    /**
     * Setar novas linhas pra scoreboard
     * @param strings array de linhas
     * @return a classe
     */
    public EasyBoard setText(String[] strings) {
        List<String> list = Arrays.asList(strings);
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) setText(list.get(i), i);
        return this;
    }

    /**
     * Setar novas linhas para scoreboard
     * @param string linha
     * @param score numero
     * @return a classe
     */
    public EasyBoard setText(String string, int score) {
        if (score > 16) throw new Error("Os scores sao maiores que 15.");
        if (string.length() > 32) throw new Error("O texto da linha " + score + " e maior que 32 chars."); // Em breve, irei colocar para 48!!

        Iterator<String> iterator = Splitter.fixedLength(16).split(string).iterator();
        String name = getTeamName(score);
        String player = getScoreName(score);

        if (getScoreboard().getTeam(name) == null) {
            Team team = getScoreboard().registerNewTeam(name);
            if (!team.hasEntry(player)) {
                team.addEntry(player);
                team.setPrefix(iterator.next());
                if (string.length() > 16) team.setSuffix(ChatColor.getLastColors(string) + iterator.next());
            }
        } else {
            getObjective().getScore(player).setScore(score);
            Team team = getScoreboard().getTeam(name);
            team.setPrefix(iterator.next());
            if (string.length() > 16) team.setSuffix(ChatColor.getLastColors(string) + iterator.next());
        }

        return this;
    }

    /**
     * Metodo para atualizar a scoreboard
     */
    public abstract void update();

    /**
     * Remover texto
     * @param score linha
     */
    public void removeText(int score) {
        String name = getTeamName(score);
        String player = getScoreName(score);
        getScoreboard().resetScores(player);
        if (getScoreboard().getTeam(name) != null) getScoreboard().getTeam(name).unregister();
    }

    /**
     * Remover scoreboard
     */
    public void removeScoreboard() {
        if (!getScoreboards().containsKey(getPlayer())) throw new Error("O jogador nao possui scoreboard associada.");
        EasyBoard scoreboard = getScoreboards().get(getPlayer());
        scoreboard.resetTeams();
        scoreboard.resetObjectives();
        getScoreboards().remove(getPlayer());
    }

    /**
     * Obter nome do time
     * @param score score da linha
     * @return string do time
     */
    public String getTeamName(int score) {
        return "[Team-" + score;
    }

    /**
     * Obter linha
     * @param i onde a linha esta
     * @return string da linha
     */
    private String getScoreName(int i) {
        ChatColor color = ChatColor.values()[i];
        return String.format("%s" + color.getChar() + ChatColor.RESET, ChatColor.COLOR_CHAR);
    }

    /**
     * Checa se a um texto na respectiva score
     * @param score Integer para pegar o texto
     * @return Retorna o estado de check
     */
    public boolean hasText(int score) {
        return getScoreboard().getTeam(getTeamName(score)) != null;
    }

    /**
     * Reseta todos os textos da scoreboard
     */
    public void reset() {
        for (int i = 0; i <= 15; i++) removeText(i);
    }

    /**
     * Reseta todos os times da scoreboard
     */
    public void resetTeams() {
        for (Team team : getScoreboard().getTeams()) team.unregister();
    }

    /**
     * Reseta todos os objectives da scoreboard
     */
    public void resetObjectives() {
        for (Objective objective : getScoreboard().getObjectives()) objective.unregister();
    }

}
