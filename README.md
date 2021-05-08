#Thunder Child
A GUI java twitter scraper by withp.

It's named Thunder Child because twitter API is a WotW tripod.

Outputs either to CSV or to go1den STM format.

#Method of Operation
##Twitter Scrape
Given a username, a start date, and an end date, we must search a new page for the user's tweets on each day.

Then we have to clean up the resulting mess.

Output is to a CSV file where the 1st column is username, 2nd column is date, 3rd column is tweet.

##CSVtoSTM
Stm files are somewhat clunky JSON.
Because the target column here is always the 3rd column,
we just grab each 3rd column and make a json entry for it.

We'll want a csv merge eventually too...

##Youtube Comment Scrape
Coming next version.

#Other Notes
Currently only runs on windows because I haven't got selenium set up cross OS. 
That will be fixed ~soon~

Also it's the ugliest program ever, I'm aware. That will also be fixed at some point.