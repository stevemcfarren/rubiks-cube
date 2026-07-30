# Rubik's Cube Solver

## Project Description

The purpose of this project is to model a Rubik's Cube and develop an algorithm that can solve a cube from any state quickly.  My interest in this project started back in college when I did a similar project in my "Data Structures and Algorithms" course.  For that course I successfully solved a Rubik's Cube using a brute force algorithm that tested all possible combinations of moves until the cube was solved.  While it theoretically would have solved any cube, the performance was O(12^n) so if the solution required more than 10 moves it took forever.

After focusing for the last several years on higher level design and management, I decided I wanted to get back into lower level development.  The project I did in college always intrigued me, so it seemed like an interesting and challenging spare time project to rebuild my coding skills.  

## The Model

The Rubik's Cube will be modeled as a collection of 26 3-dimensional pieces on a 3x3x3 grid.  Each piece is described by a set of colors for it's visible sides along with a 3D point representing it's location within the cube.  The center of the cube will be considered to be at location (0, 0, 0) on the grid.

There are three types of pieces in a Rubik's Cube:
- Six center pieces make up the center of each face.  They have one visible side and therefore one color.  Their location will have exactly one axis that is not zero.  A center piece's location is fixed relative to the other center pieces.  Rotating a face does not change the center pieces location.
- Eight corner pieces make up the corners of the cube.  They have three visible sides and therefore three colors.  Their location cannot have any axes that are equal to zero.
- Twelve edge pieces are the pieces between the corners and adjacent to the center pieces.  They have two visible sides and therefore two colors.  Their location will have exactly one axis that is equal to zero.

Since the center pieces cannot move relative to the other pieces in the cube, we will describe each face by the color of the center piece in that face.  For example, the 'blue face' would be the face with the blue center piece regardless of the colors of the other eight pieces currently in that face.

Note that each piece can be uniquely described by it's color(s).  For example, there is only one blue center piece, one blue and red edge piece, and one blue, red and yellow corner piece.

Since the location of the center pieces are fixed, that means the 'solved' location for each movable pieces is fixed also.  For example, the blue and red edge piece can only be considered to be 'solved' if it is on the edge shared by the blue and red faces, with it's blue side on the blue face and it's red side on the red face.

## The Solver

The methodical approach to solving a Rubik's Cube is to solve it by layers.  First solve the top row by solving the top edge pieces followed by the top corners.  Next, solve the middle row edge pieces.  Finally, solve the bottom corners followed by the bottom edges.  For the top two rows, individual pieces can be solved without impacting other already solved pieces.  For the bottom row, it's not possible to solve one piece without impacting other similar pieces.  Instead, each set of four pieces (corners or edges) must be solved together.

For each stage there are repeatable sequences that can be used to solve the pieces in that stage.  The goal of this program is to first build a library of these "rules" and then implement a solver that can use the rules to solve a Rubik's Cube from any state.

### The Rule Finder

The rule finder ([RuleFinder](src/main/java/com/stevemcfarren/rubikscube/rules/RuleFinder.java)) was designed to test all possible sequences of moves and record sequences that could be useful in solving a cube.  The rule finder is a breadth-first search algorithm that tests all possible sequences of length 1, followed by all sequences of length 2 and so on.  The number of possible sequences grows exponentially with length, so this algorithm is O(12^n).  Rough testing showed a run time of approximately 5 minutes for max length of 9 and roughly 10x increase in time with each increase in max length.  With rules expected to be up to 12 moves in length, the rule finder would take roughly 83 hours (3.5 days) to run.  With this in mind, the rule finder divides the job into batches so that it can be stopped between batches and resume later with the next batch.  The job could also be divided between machines/processes with the results from each process combined at the end.

The rule finder works backwards by starting with a solved cube and applying every possible combination of moves (up to the configured max length).  After each move, it compares the current state to the initial (solved) state to determine what pieces were impacted by the sequence of moves.  If the impact matches one of the criteria the rule finder is looking for, the reverse sequences of moves is recorded as a possible rule.  The types of rules and their criteria are outlined below.  Note that the rule finder in same cases recorded extra rules that might be needed to minimize the chances of having to run the rule finder again with additional criteria.  These unneeded rules were later filtered out by the rule processor ([RuleProcessor](src/main/java/com/stevemcfarren/rubikscube/rules/RuleProcessor.java)).

#### Solve Top Front Edge

To solve the top edges we will look for rules that move the piece that belongs in the top front edge from it's current location to it's solved state without disturbing the other top edge pieces.  The rule finder will only find rules to solve the top front edge.  The other edges can be solved using the same rules by first rotating the cube such that the edge to be solved is in the top front position.  Note that these rules should not impact the other three top edges unless the piece that belongs in the top front edge is located in one of the other top edge locations.

There are 12 edge pieces in a Rubik's Cube and each edge can be in one of two orientations.  This gives 24 possibles states for any edge pieces, with one of those states being the solved state.  Therefore, there are 23 rules that must be found to be be able to solve the top front edge in all cases.

#### Solve Top Right Front Corner

Similar to the top edges, the rule finder must find rules to solve the top right front corner.  The top edges are expected to be solved first, so top right front corner rules must not disturb the top edges or the other top corners.  There are 8 corner pieces in a Rubik's Cube and each edge can be in one of three orientations.  This gives 24 possibles states for any corner piece, with one of those states being the solved state.  Therefore, there are 23 rules that must be found to be be able to solve the top right front corner in all cases.

#### Solve Right Front Edge

Similar to the top row pieces, the rule finder must find rules to solve the right front edge.  The top row is expected to be solved first, so right front edge rules must not disturb any top row pieces or the other middle edges.  There are 12 edge pieces in a Rubik's Cube, but the top edge positions have already been solved.  The right front edge can then be in one of 8 remaining edge positions and two orientations, giving 15 rules that must be found to solve the right front edge in all cases.

#### Solve Bottom Corners

To solve the bottom corners, the rule finder will look for two types of rules.  The first will be sequences that swap the position of multiple bottom corners ignoring orientation.  The second will be sequences that rotate one or more bottom corners without changing the position of any bottom corner.

##### Bottom Corner Swap

To simplify the set of rules, it was decided to adopt a convention that the back left bottom corner must already be in position.  This is accomplished simply by rotating the bottom face until the condition is met.  With the back left corner in place, there are only five rules needed to position the other three corners:
* Left corner swap
* Front corner swap
* Opposite corner swap (back left with front right)
* Three corner swap to the left
* Three corner swap to the right

The rule finder was designed to record all sequences that changed the position of any bottom corners (except the back left) without impacting the top two rows.  Therefore, the rule finder found more than five rules as it recorded duplicates that rotated the pieces in different ways during the swap.  These duplicates were later determined to be not necessary and were filtered out by the rule processor.

##### Bottom Corner Rotate

Corner swaps should be performed first, so the criteria for corner rotate rules is that none of the corners changed position but at least one corner was rotated.  Each of the four corners can (theoretically) be in one of three orientations, so it seems there should be 81 possible scenarios (3^4).  However, not all scenarios are possible because the sum of rotation of all four corners must balance (add up to zero or one full rotation).  Therefore 2/3 of the 81 scenarios are not possible, leaving 27 scenarios.  One of the 27 scenarios is the solved state so 26 rules are needed.

The rule finder found 22 rules with max length of 12.  Analyzing the found rules, it was determined the remaining scenarios could be handled by combining two found rules.

#### Solve Bottom Edges

To solve the bottom edges, the rule finder originally looked for the same two types of rules as for the bottom corners.  No in-place rotate rules were found and it was determined that the found edge swap rules were sufficient so edge rotate rule criteria was eliminated.

There are theoretically 384 possible bottom edge scenarios (4! * 2^4).  Only 1/4 of these scenarios are possible because you cannot flip a single edge piece in place and you cannot swap just two edges.  That leaves 96 possible scenarios (384/4).  One of the 96 scenarios is the solved state so 95 rules are needed.

The rule finder found 41 rules with max length of 12.  Analyzing the found rules, it was determined the remaining scenarios could be handled by combining two found rules.

## Testing

There are \~43 quintillion (43*10^18) reachable states in a Rubik's Cube, so it is not possible to test the solver with even a low percentage of these states.  To test the solver and help flush out additional rules, a JUnit test was constructed to test the solver against 10,000 random cubes.  With the initial set of rules found by the rule finder, this testing proved that \~85% of cube could be solved through the bottom corners and \~40% of cubes could be solved completely.  Once the rule processor was created to combine rules and the expected number of rules were found, this test has successfully solved all 10,000 random cubes each time it was run.  The fact that the rule finder and processor found the exact number of rules theorized to be needed, plus JUnit tests successfully executed against tens of thousands of random cubes, gives reasonable confidence that the solver can solve any possible Rubik's Cube.
