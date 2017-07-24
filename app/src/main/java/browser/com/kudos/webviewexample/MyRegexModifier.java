package browser.com.kudos.webviewexample;

import com.github.rwitzel.streamflyer.core.AfterModification;
import com.github.rwitzel.streamflyer.core.Modifier;
import com.github.rwitzel.streamflyer.internal.thirdparty.ZzzAssert;
import com.github.rwitzel.streamflyer.regex.MatchProcessor;
import com.github.rwitzel.streamflyer.regex.MatchProcessorResult;
import com.github.rwitzel.streamflyer.regex.OnStreamMatcher;
import com.github.rwitzel.streamflyer.regex.OnStreamStandardMatcher;
import com.github.rwitzel.streamflyer.regex.ReplacingProcessor;
import com.github.rwitzel.streamflyer.regex.addons.stateful.StateMachine;
import com.github.rwitzel.streamflyer.util.ModificationFactory;
import com.github.rwitzel.streamflyer.util.ModifyingReaderFactory;
import com.github.rwitzel.streamflyer.util.ModifyingWriterFactory;
import com.github.rwitzel.streamflyer.util.statistics.LineColumnAwareModificationFactory;
import com.github.rwitzel.streamflyer.util.statistics.PositionAwareModificationFactory;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds text that matches a given regular expression. The match is processed by the given {@link MatchProcessor}. The
 * default match processor replaces the matched texts with the configured replacements.
 * <p>
 * <h1>Contents</h1>
 * <p>
 * <b> <a href="#g1">1. How do I use this modifier?</a><br/>
 * <a href="#g2">2. Instead of replacing text I want to do something else when the regular expression matches. How can I
 * do this?</a><br/>
 * <a href="#g3">3. How can I find out the position of the matches within the stream?</a> <br/>
 * <a href="#g4">4. How much memory does the modifier consume?</a><br/>
 * <a href="#g5">5. Which features of Java's Pattern are not supported yet?</a><br/>
 * <a href="#g6">6. Which features of Java's Matcher are not supported yet?</a><br/>
 * <a href="#g7">7. How do I configure RegexModifier if my pattern contains ^ or \b or \B?</a><br/>
 * <a href="#g8">8. What value to choose for the constructor parameter <code>minimumLengthOfLookBehind</code>?</a><br/>
 * <a href="#g9">9. What value to choose for the constructor parameter <code>newNumberOfChars</code>?</a><br/>
 * <a href="#g10">10. Can I share RegexModifier instances between threads?</a><br/>
 * </b> <!-- ++++++++++++++++++++++++++++++ -->
 * <p>
 * <h3 id="g1">1. How do I use this modifier?</h3>
 * <p>
 * EXAMPLE:
 * <code><pre class="prettyprint lang-java">// choose the character stream to modify
 Reader originalReader = new StringReader("edit stream");

 // define the regular expression and the replacement
 Modifier myModifier = new RegexModifier("edit stream", 0, "modify stream");

 // create the modifying reader that wraps the original reader
 Reader modifyingReader = new ModifyingReader(originalReader, myModifier);

 // use the modifying reader instead of the original reader
 String output = IOUtils.toString(modifyingReader);
 assertEquals("modify stream", output);</pre></code>
 * <h3 id="g2">2. Instead of replacing text I want to do something else when the regular expression matches. How can I
 * do this?</h3>
 * <p>
 * Implement your own {@link MatchProcessor}. In the following example the processor prints the matched text on the
 * console.
 * <p>
 * EXAMPLE: <code><pre class="prettyprint lang-java">class MatchPrinter implements MatchProcessor {

 public MatchProcessorResult process(StringBuilder characterBuffer,
 int firstModifiableCharacterInBuffer, MatchResult matchResult) {

 // print the matches text
 System.out.println("match: " + matchResult.group());

 // continue matching behind the end of the matched text
 return new MatchProcessorResult(matchResult.end(), true);
 }
 }
 // ...
 Modifier myModifier = new RegexModifier("^.*ERROR.*$", Pattern.MULTILINE, new MatchPrinter(), 0, 2048);</pre></code>
 * <h3 id="g3">3. How can I find out the position of the matches within the stream?</h3>
 * <p>
 * You must count the characters that you skip. You can do this by subclassing {@link com.github.rwitzel.streamflyer.regex.RegexModifier} and overwrite
 * {@link Modifier#modify(StringBuilder, int, boolean)}.
 * <p>
 * You might find {@link LineColumnAwareModificationFactory} and {@link PositionAwareModificationFactory} helpful as
 * well.
 * <h3 id="g4">4. How much memory does the modifier consume?</h3>
 * <p>
 * The maximum buffer size used by this modifier does not exceed the size of the longest match by factor three. EXAMPLE:
 * Assume in your stream the longest match contains 100K characters. Then the internally used buffer is at no time
 * larger than 300K characters. But this rule does not apply if you use greedy operators. If you use greedy operators,
 * the entire stream content might be loaded into the memory at once. The web page of this project gives <a href=
 * "https://github.com/rwitzel/streamflyer/blob/wiki/AdvancedRegularExpressionsExample.md" >more details</a>.
 * <h3 id="#g5">5. Which features of Java's Pattern are not supported yet?</h3>
 * <p>
 * Apart from \G (the boundary matcher that matches the end of the previous match) all other features mentioned in
 * {@link Pattern} are supported.
 * <h3 id="#g6">6. Which features of Java's Matcher are not supported yet?</h3>
 * <p>
 * Java's {@link Matcher} allows the user to {@link Matcher#usePattern(Pattern) change} the used pattern. RegexModifier
 * does not enables this out-of-the-box but {@link StateMachine} provides that functionality.
 * <p>
 * Preferences that are region-related (like anchoring bounds, transparent bounds) and preferences that determine how to
 * continue the matching do not have to be configured because they are already setup in an appropriate way by the
 * RegexModifier.
 * <h3 id="#g7">7. How do I configure RegexModifier if my pattern contains ^ or \b or \B?</h3>
 * <p>
 * These pattern constructs require a look-behind. Therefore, set <code>minimumLengthOfLookBehind</code> at least to
 * one. As RegexModifier modifies the input, look-behind constructs may not lead to the expected result (see project
 * page). Therefore, try to replace the look-behind construct with a normal group, like "\n" for "^" and "\W" for "\b".
 * <h3 id="#g8">8. What value to choose for the constructor parameter <code>minimumLengthOfLookBehind</code>?</h3>
 * <p>
 * If your pattern does not contain zero-width look-behind constructs, then you can choose any value. Zero is
 * recommended.
 * <p>
 * Choose one if your pattern contains zero-width look-behind constructs like ^ or \b or \B but not constructs like
 * <code>(?&lt;=X)</code> or <code>(?&lt;!X)</code>.
 * <p>
 * If your pattern contains zero-width look-behind constructs like <code>(?&lt;=X)</code> or <code>(?&lt;!X)</code>, you
 * have to find out how many characters the matcher needs at least to match properly by looking behind. Use this number
 * as value for <code>minimumLengthOfLookBehind</code>. EXAMPLE: Let's say your pattern might check for
 * <code>(?&lt;=a{3})</code> and <code>(?&lt;=b{5})</code> in front of the actual match. Then the appropriate value is
 * five. If you set the value to a lower value, then there is no guarantee that the modifier matches properly.
 * <h3 id="#g9">9. What value to choose for the constructor parameter <code>newNumberOfChars</code>?</h3>
 * <p>
 * This value determines how many characters are usually processed at once. If the value is too high or too low, the
 * performance decreases. The optimal value depends on the length and number of matches in the stream.
 * <h3 id="#g10">10. Can I share RegexModifier instances between threads?</h3>
 * <p>
 * No, a RegexModifier instance is not thread-safe. Use thread-specific instances.
 *
 * @author rwoo
 * @since 18.06.2011
 */
public class MyRegexModifier implements Modifier {

    // * <p>
    // * <h2>A summary of the internal algorithm (some details are left
    // out)</h2>
    // * <p>
    // * Is there a match in the buffer?
    // * <ul>
    // * <li>we found a match
    // * <ul>
    // * <li>entire buffer matches and end of stream not hit yet -> the match
    // might
    // * change with more input -> FETCH_MORE_INPUT (<i>match_open</i>)
    // * <li>replace the matched content, and then
    // * <ul>
    // * <li>the match processor decides that no skip is needed yet -> continue
    // with
    // * the existing buffer content -> try another match
    // (<i>match_n_continue</i>)
    // * <li>skip needed but no characters left in the buffer after the
    // replacement ->
    // * MODIFY_AGAIN_IMMEDIATELY is only thing we can do
    // (<i>match_n_refill</i>)
    // * <li>skip needed and there are characters left in the buffer -> SKIP
    // * (<i>match_n_skip</i>)
    // * </ul>
    // * </ul>
    // * <li>we haven't found a match.
    // * <ul>
    // * <li>By looking for matches (including the empty string) that start in
    // the
    // * range [from, maxFrom], the end of the buffer is hit.
    // * <ul>
    // * <li>end of stream hit -> no match possible -> SKIP the entire buffer
    // * (<i>nomatch_eos</i>)
    // * <li>end of stream not hit -> match might be possible or end of buffer
    // is hit
    // * -> FETCH_MORE_INPUT cannot be wrong (<i>nomatch_fetch</i>)
    // * </ul>
    // * <li>We did not match a single character or the empty string -> SKIP the
    // * entire buffer (<i>nomatch_skip</i>)
    // * </ul>
    // * </ul>

    //
    // injected
    //

    protected ModificationFactory factory;

    protected MatchProcessor matchProcessor;

    /**
     * The compiled representation of a regular expression. If the regular expression matches, then a modification shall
     * be carried out.
     */
    protected OnStreamMatcher matcher;

    protected int newNumberOfChars = -1;

    //
    // state
    //

    /**
     * The number of characters that shall be skipped automatically if the modifier is called the next time.
     * <p>
     * This property is either zero or one (not greater). If this property is one, then the modifier tries to match
     * behind the first modifiable character. If this property is zero, then the modifier tries to match before the
     * first modifiable character.
     */
    private int unseenCharactersToSkip = 0;

    //
    // constructors
    //

    /**
     * Only for subclasses.
     */
    protected MyRegexModifier() {
        super();
    }

    /**
     * Like {@link com.github.rwitzel.streamflyer.regex.RegexModifier#RegexModifier(String, int, String, int, int)} but uses defaults for
     * <code>minimumLengthOfLookBehind</code> (1) and <code>newNumberOfChars</code> (2048).
     */
    public MyRegexModifier(String regex, int flags, String replacement) {
        this(regex, flags, replacement, 1, 2048);
    }

    public MyRegexModifier(Pattern regex, String replacement) {
        this(regex, replacement, 1, 2048);
    }

    /**
     * Creates a modifier that matches a regular expression on character streams and replaces the matches.
     * <p>
     * This modifier uses {@link OnStreamStandardMatcher} which is not the fastest implementation of
     * {@link OnStreamMatcher}. If you want to use a faster matcher, use
     * {@link #MyRegexModifier(OnStreamMatcher, MatchProcessor, int, int)} instead.
     * <p>
     * A more convenient use of a {@link com.github.rwitzel.streamflyer.regex.RegexModifier} is provided by the {@link ModifyingReaderFactory} respectively
     * {@link ModifyingWriterFactory}.
     *
     * @param regex
     *            the regular expression that describe the text that shall be replaced. See
     *            {@link Pattern#compile(String, int)}.
     * @param flags
     *            the flags that are to use when the regex is applied on the character stream. See
     *            {@link Pattern#compile(String, int)}.
     * @param replacement
     *            the replacement for the text that is matched via <code>regex</code>. See
     *            {@link Matcher#appendReplacement(StringBuffer, String)}.
     * @param minimumLengthOfLookBehind
     *            See {@link com.github.rwitzel.streamflyer.regex.RegexModifier#RegexModifier(OnStreamMatcher, MatchProcessor, int, int)} .
     * @param newNumberOfChars
     *            See {@link com.github.rwitzel.streamflyer.regex.RegexModifier#RegexModifier(OnStreamMatcher, MatchProcessor, int, int)} .
     */
    public MyRegexModifier(String regex, int flags, String replacement, int minimumLengthOfLookBehind,
                           int newNumberOfChars) {
        this(regex, flags, new ReplacingProcessor(replacement), minimumLengthOfLookBehind, newNumberOfChars);
    }

    public MyRegexModifier(Pattern pattern, String replacement, int minimumLengthOfLookBehind,
                           int newNumberOfChars) {
        this(pattern, new ReplacingProcessor(replacement), minimumLengthOfLookBehind, newNumberOfChars);
    }

    /**
     * See {@link #MyRegexModifier(String, int, String, int, int)}.
     */
    public MyRegexModifier(String regex, int flags, MatchProcessor matchProcessor, int minimumLengthOfLookBehind,
                           int newNumberOfChars) {

        Matcher jdkMatcher = Pattern.compile(regex, flags).matcher("");
        jdkMatcher.useTransparentBounds(true);
        jdkMatcher.useAnchoringBounds(false);
        init(new OnStreamStandardMatcher(jdkMatcher), matchProcessor, minimumLengthOfLookBehind, newNumberOfChars);
    }

    public MyRegexModifier(Pattern regex, MatchProcessor matchProcessor, int minimumLengthOfLookBehind,
                           int newNumberOfChars) {

        Matcher jdkMatcher = regex.matcher("");
        jdkMatcher.useTransparentBounds(true);
        jdkMatcher.useAnchoringBounds(false);
        init(new OnStreamStandardMatcher(jdkMatcher), matchProcessor, minimumLengthOfLookBehind, newNumberOfChars);
    }

    /**
     * Creates a modifier that matches a regular expression on character streams and does 'something' if matches are
     * found.
     * <p>
     *
     * @param matcher
     *            Matches a regular expression on a <code>CharSequence</code>.
     * @param matchProcessor
     *            Defines what to do if the regular expression matches some text in the stream.
     * @param minimumLengthOfLookBehind
     *            See {@link AfterModification#getNewMinimumLengthOfLookBehind()}.
     * @param newNumberOfChars
     *            See {@link AfterModification#getNewNumberOfChars()}. This should not be smaller than the length of the
     *            characters sequence the pattern needs to match properly. In case you want to match more than once, the
     *            value should be higher.
     */
    public MyRegexModifier(OnStreamMatcher matcher, MatchProcessor matchProcessor, int minimumLengthOfLookBehind,
                           int newNumberOfChars) {

        init(matcher, matchProcessor, minimumLengthOfLookBehind, newNumberOfChars);
    }

    @SuppressWarnings("hiding")
    protected void init(OnStreamMatcher matcher, MatchProcessor matchProcessor, int minimumLengthOfLookBehind,
                        int newNumberOfChars) {

        this.factory = new ModificationFactory(minimumLengthOfLookBehind, newNumberOfChars);
        this.matchProcessor = matchProcessor;
        this.matcher = matcher;
        this.newNumberOfChars = newNumberOfChars;
    }

    //
    // interface Modifier
    //

    /**
     * @see com.github.rwitzel.streamflyer.core.Modifier#modify(java.lang.StringBuilder, int, boolean)
     */
    @Override
    public AfterModification modify(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
                                    boolean endOfStreamHit) {

        // the first position we will match from.
        Integer minFrom = null;

        while (true) {

            // determine the range [minFrom, maxFrom] that will contain the
            // first character of the matching string

            if (minFrom == null) {
                minFrom = firstModifiableCharacterInBuffer;

                if (unseenCharactersToSkip > 0) {

                    // is there at least one modifiable character in the buffer?
                    if (minFrom + unseenCharactersToSkip > characterBuffer.length()) {
                        // no -> we need more input to skip the characters

                        if (endOfStreamHit) {
                            // -> stop
                            return factory.stop(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit);
                        } else {
                            // -> fetch more input
                            return factory.fetchMoreInput(0, characterBuffer, firstModifiableCharacterInBuffer,
                                    endOfStreamHit);
                        }

                    } else {
                        // yes -> increase the *minFrom*
                        minFrom += unseenCharactersToSkip;
                        unseenCharactersToSkip = 0;
                    }
                }
            }

            // we have to restrict maxFrom in order to prevent that the
            // requested number of characters increases more and more
            int maxFrom = firstModifiableCharacterInBuffer + newNumberOfChars;

            // adjust maxFrom if it is bigger than the given buffer
            if (maxFrom > characterBuffer.length()) {
                // this is NOT set to characterBuffer.length() -1 by intention
                // as a regular expression might match on the zero length string
                // (but with positive look-behind)!
                maxFrom = characterBuffer.length();
            }

            // find first match
            // (as the match processor might have modified the buffer, we reset
            // the matcher inside the loop instead of outside of the loop)
            matcher.reset(characterBuffer);
            boolean matchFound = matcher.findUnlessHitEnd(minFrom, maxFrom);

            if (matchFound) {
                // we found a match

                // could change this positive match into a negative one
                // (matcher.requireEnd()) or into a longer one (greedy
                // operator)?
                if (matcher.hitEnd() && !endOfStreamHit) {
                    // (match_open) yes, it could -> we need more input

                    int numberOfCharactersToSkip = matcher.lastFrom() - firstModifiableCharacterInBuffer;

                    // read more input (skip some characters if possible)
                    AfterModification mod = factory.fetchMoreInput(numberOfCharactersToSkip, characterBuffer,
                            firstModifiableCharacterInBuffer, endOfStreamHit);

                    assert __checkpoint( //
                            "name", "match_open", //
                            "minLen", firstModifiableCharacterInBuffer, //
                            "characterBuffer", characterBuffer, //
                            "endOfStreamHit", endOfStreamHit, //
                            "afterModification", mod);

                    return mod;
                } else {
                    // no -> thus we can use this match -> process the match

                    // process the match
                    MatchResult matchResult = matcher; // .toMatchResult()?
                    // (I could pass firstModifiableCharacterInBuffer instead of
                    // minFrom as well)
                    MatchProcessorResult matchProcessorResult = matchProcessor.process(characterBuffer, minFrom,
                            matchResult);
                    minFrom = matchProcessorResult.getFirstModifiableCharacterInBuffer();

                    // match again without skip? (even for minFrom == maxFrom we
                    // try a match) (minFrom <= maxFrom is needed so that the
                    // buffer does not increase if the replacement is longer
                    // than the replaced string, i.e. minFrom > maxFrom means
                    // that a SKIP is needed)
                    // (I (rwoo) think an earlier SKIP (minFrom < maxFrom
                    // instead of minFrom <= maxFrom) would also be possible.
                    // This has no impact on the matching and only minimal
                    // impact on the performance)
                    if (minFrom <= maxFrom && matchProcessorResult.isContinueMatching()) {
                        // (match_n_continue) no skip needed yet -> continue
                        // matching on the existing buffer content

                        assert __checkpoint( //
                                "name", "match_n_continue", //
                                "minLen", firstModifiableCharacterInBuffer, //
                                "characterBuffer", characterBuffer, //
                                "endOfStreamHit", endOfStreamHit, //
                                "minFrom", minFrom);

                        // We try the next match on the modified input, i.e.
                        // not match only once -> next loop
                        continue;
                    } else {

                        // we shall not continue matching on the
                        // existing buffer content but skip (keep the buffer
                        // small)

                        int numberOfCharactersToSkip;
                        if (minFrom > characterBuffer.length()) {
                            // this happens when we avoid endless loops after
                            // we matched an empty string
                            unseenCharactersToSkip = minFrom - characterBuffer.length();
                            ZzzAssert.isTrue(unseenCharactersToSkip == 1, "unseenCharactersToSkip must be one but was "
                                    + unseenCharactersToSkip);
                            numberOfCharactersToSkip = characterBuffer.length() - firstModifiableCharacterInBuffer;
                        } else {
                            numberOfCharactersToSkip = minFrom - firstModifiableCharacterInBuffer;
                        }

                        if (numberOfCharactersToSkip == 0) {

                            // (match_n_refill) there are no characters left in
                            // the buffer after the replacement ->
                            // MODIFY_AGAIN_IMMEDIATELY is only thing we can do
                            // (the match processor implementation must not
                            // cause an endless loop)

                            // (passing false for endOfStreamHit is ugly!!!)
                            // we should offer a new method in
                            // ModificationFactory, something like
                            // continueAfterModification(...) that chooses the
                            // appropriate action. the following code is always
                            // a MODIFY_AGAIN_IMMEDIATELY
                            AfterModification mod = factory.fetchMoreInput(numberOfCharactersToSkip, characterBuffer,
                                    firstModifiableCharacterInBuffer, false);

                            assert __checkpoint( //
                                    "name", "match_n_refill", //
                                    "minLen", firstModifiableCharacterInBuffer, //
                                    "characterBuffer", characterBuffer, //
                                    "endOfStreamHit", endOfStreamHit, //
                                    "afterModification", mod);

                            return mod;
                        } else {
                            // (match_n_skip) there are characters left in
                            // the buffer -> SKIP

                            AfterModification mod = factory.skipOrStop(numberOfCharactersToSkip, characterBuffer,
                                    firstModifiableCharacterInBuffer, endOfStreamHit);

                            assert __checkpoint( //
                                    "name", "match_n_skip", //
                                    "minLen", firstModifiableCharacterInBuffer, //
                                    "characterBuffer", characterBuffer, //
                                    "endOfStreamHit", endOfStreamHit, //
                                    "afterModification", mod);

                            return mod;
                        }
                    }

                }
            } else {
                // we haven't found a match

                // By looking for matches (including the empty string) that
                // start in the range [from, maxFrom], is the end of the buffer
                // hit?
                if (matcher.lastFrom() <= maxFrom) {
                    // yes, the end of the buffer was hit

                    // can we get more input?
                    if (endOfStreamHit) {
                        // (nomatch_eos) no, in the entire stream we will not
                        // found more matches that start in range [from,
                        // maxFrom] -> skip the characters from range [from,
                        // maxFrom]

                        int numberOfCharactersToSkip = maxFrom - firstModifiableCharacterInBuffer;
                        AfterModification mod = factory.skipOrStop(numberOfCharactersToSkip, characterBuffer,
                                firstModifiableCharacterInBuffer, endOfStreamHit);

                        assert __checkpoint( //
                                "name", "nomatch_eos", //
                                "minLen", firstModifiableCharacterInBuffer, //
                                "characterBuffer", characterBuffer, //
                                "endOfStreamHit", endOfStreamHit, //
                                "afterModification", mod);

                        return mod;

                    } else {
                        // (nomatch_fetch) yes > we should fetch more input
                        // (because end of stream is not hit yet)

                        // if maxFrom == characterBuffer.length() and lastFrom()
                        // == maxFrom we cannot decide whether this is really an
                        // open match or rather a not a match at all. But by
                        // skipping the characters in front of lastFrom() and
                        // fetching more input we cannot do anything wrong

                        int numberOfCharactersToSkip = matcher.lastFrom() - firstModifiableCharacterInBuffer;
                        AfterModification mod = factory.fetchMoreInput(numberOfCharactersToSkip, characterBuffer,
                                firstModifiableCharacterInBuffer, endOfStreamHit);

                        assert __checkpoint( //
                                "name", "nomatch_fetch", //
                                "minLen", firstModifiableCharacterInBuffer, //
                                "characterBuffer", characterBuffer, //
                                "endOfStreamHit", endOfStreamHit, //
                                "afterModification", mod);

                        return mod;
                    }

                } else { // matcher.lastFrom() == maxFrom + 1

                    // (nomatch_skip) no, we are matching not a single character

                    // -> skip the characters from range [from, maxFrom]
                    int numberOfCharactersToSkip = maxFrom - firstModifiableCharacterInBuffer;
                    AfterModification mod = factory.skipOrStop(numberOfCharactersToSkip, characterBuffer,
                            firstModifiableCharacterInBuffer, endOfStreamHit);

                    assert __checkpoint( //
                            "name", "nomatch_skip", //
                            "minLen", firstModifiableCharacterInBuffer, //
                            "characterBuffer", characterBuffer, //
                            "endOfStreamHit", endOfStreamHit, //
                            "afterModification", mod);

                    return mod;
                }
            }
        }

    }

    /**
     * This method is called if a certain line of code is reached ("checkpoint").
     * <p>
     * This method should be called only if the modifier is tested. Otherwise you might experience performance
     * penalties.
     *
     * @param checkpointDescription
     *            A list of objects describing the checkpoint. The objects should be given as name-value-pairs.
     * @return Returns true. This allows you to use this method as side-effect in Java assertions.
     */
    protected boolean __checkpoint(Object... checkpointDescription) {
        // nothing to do here
        return true;
    }

    //
    // override Object.*
    //

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RegexModifier [\nfactory=");
        builder.append(factory);
        builder.append(", \nreplacement=");
        builder.append(matchProcessor);
        builder.append(", \nmatcher=");
        builder.append(matcher);
        builder.append(", \nnewNumberOfChars=");
        builder.append(newNumberOfChars);
        builder.append("]");
        return builder.toString();
    }

    //
    // injected
    //

    /**
     * @param matchProcessor
     *            The {@link #matchProcessor} to set.
     */
    public void setMatchProcessor(MatchProcessor matchProcessor) {
        this.matchProcessor = matchProcessor;
    }

}
