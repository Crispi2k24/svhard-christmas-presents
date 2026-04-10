package pl.svhard.christmas.presents.commands.argument;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import pl.svhard.christmas.presents.present.Present;
import pl.svhard.christmas.presents.present.PresentId;
import pl.svhard.christmas.presents.present.PresentIdMapper;
import pl.svhard.christmas.presents.present.PresentService;

public class PresentArgument extends ArgumentResolver<CommandSender, Present> {

    private final PresentService presentService;
    private final PresentIdMapper idMapper;

    public PresentArgument(PresentService presentService, PresentIdMapper idMapper) {
        this.presentService = presentService;
        this.idMapper = idMapper;
    }

    @Override
    protected ParseResult<Present> parse(
        Invocation<CommandSender> invocation,
        Argument<Present> argument,
        String string
    ) {
        try {
            int numericId = Integer.parseInt(string);
            Optional<Present> present = this.idMapper.getPresentByNumericId(numericId);

            return present.map(ParseResult::success)
                .orElseGet(() -> ParseResult.failure("Nie znaleziono prezentu o ID: " + numericId));
        }
        catch (NumberFormatException ignored) {
            // Not a number, try UUID
        }

        try {
            PresentId presentId = PresentId.fromString(string);
            Optional<Present> present = this.presentService.getPresent(presentId);

            return present.map(ParseResult::success)
                .orElseGet(() -> ParseResult.failure("Nie znaleziono prezentu o UUID: " + string));
        }
        catch (IllegalArgumentException exception) {
            return ParseResult.failure("Nieprawidłowy format ID: " + string + ". Użyj numeru (np. 1, 2, 3) lub UUID.");
        }
    }

    @Override
    public SuggestionResult suggest(
        Invocation<CommandSender> invocation,
        Argument<Present> argument,
        SuggestionContext context
    ) {
        List<Present> presents = this.idMapper.getAllPresentsSortedByNumericId();

        return presents.stream()
            .map(present -> {
                int numericId = this.idMapper.getNumericId(present.getId()).orElse(-1);
                return String.valueOf(numericId);
            })
            .filter(id -> !id.equals("-1"))
            .collect(SuggestionResult.collector());
    }
}
