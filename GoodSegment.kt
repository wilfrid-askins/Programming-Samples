/**
* This challenge was given to me by Facebook for their London Hackathon.
* It is a good example of how to use Kotlin's Lambda features.
*
* The function will give you the longest sequence of natural numbers between l and r, excluding any bad numbers specified.
*/

/*
 * Complete the 'goodSegment' function below.
 *
 * The function is expected to return an INTEGER.
 * The function accepts following parameters:
 *  1. INTEGER_ARRAY badNumbers
 *  2. INTEGER l
 *  3. INTEGER r
 */

fun goodSegment(badNumbers: Array<Int>, l: Int, r: Int): Int {

    // Add the lower and upper boundaries to the number list
    var nums = badNumbers.toMutableList()
    nums.add(r)
    nums.add(l)

    return nums
            .filter{ it in l .. r} // Ignore numbers outside range
            .sorted() // Sort in ascending order
            .mapIndexed{ index,element ->

                // If end return 0
                if(index == nums.size-2) return 0

                // Get paired element
                val element1 = nums.getOrElse(index + 1){ 0 }
                // Calculate the range between the numbers (exclusive)
                var range = element1 - element - 1;

                // If at a boundary, make inclusive
                if(element == l || element1 == r) range += 1;

                range // return calculated value
            }
            .max()!! // Return largest range found
}
