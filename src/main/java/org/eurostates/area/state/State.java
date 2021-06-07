package org.eurostates.area.state;

import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.eurostates.EuroStates;
import org.eurostates.area.Area;
import org.eurostates.area.ESUser;
import org.eurostates.area.town.Town;
import org.eurostates.parser.Parsers;
import org.eurostates.relationship.AbstractRelationship;
import org.eurostates.relationship.Relationship;
import org.eurostates.relationship.RelationshipStatus;
import org.eurostates.relationship.war.WarRelationship;
import org.eurostates.technology.Technology;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface State extends Area {

    @NotNull Set<Town> getTowns();

    @NotNull Set<Technology> getTechnology();

    @NotNull Set<UUID> getCitizenIds();


    @NotNull CompletableFuture<Group> getOrCreateGroup();

    @NotNull Optional<Group> getGroup();

    default @NotNull Set<String> getRanks() {
        return getEuroStatesCitizens().parallelStream().map(ESUser::getRank).collect(Collectors.toSet());
    }

    @Override
    default Set<Relationship> getRelationships() {
        return EuroStates.getPlugin().getRelationships().parallelStream().filter(r -> r.getStates().contains(this)).collect(Collectors.toSet());
    }

    @Deprecated
    default @NotNull Set<String> getPermissions() {
        return this.getTechnology().parallelStream().flatMap(tech -> tech.getPermissions().parallelStream()).collect(Collectors.toSet());
    }

    default @NotNull Set<ESUser> getEuroStatesCitizens() {
        return Parsers.collectOrFilter(this.getCitizenIds().parallelStream(), Parsers.GETTER_USER::fromId, Collectors.toSet());
    }

    default @NotNull Set<OfflinePlayer> getCitizens() {
        return this
                .getCitizenIds()
                .stream()
                .map(Bukkit::getOfflinePlayer)
                .collect(Collectors.toSet());
    }

    default @NotNull Set<Player> getOnlineCitizens() {
        return this.getCitizenIds()
                .stream()
                .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer)
                .collect(Collectors.toSet());
    }
}
