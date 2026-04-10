package pl.svhard.christmas.presents.present;

import com.eternalcode.commons.bukkit.position.Position;

public class PresentAlreadyExistsException extends RuntimeException {

    public PresentAlreadyExistsException(Position location) {
        super("Present already exists at location: " + location);
    }
}
