# Thunder Child
A GUI java twitter scraper by withp.

It's named Thunder Child because twitter API is a WotW tripod.

Outputs either to CSV or to go1den STM format.

# Method of Operation
## Twitter Scrape
Given a username, a start date, and an end date, Thunder Child searches a new page for the user's tweets on each day via browser selenium.

Output is to a CSV file where the 1st is the tweet itself and other columns are date/author

## CSVtoSTM
Stm files are somewhat clunky JSON, but given a .csv this will make a .stm of the first column as text messages.

In addition, if one instead selects 2 .csv files, one can merge the 2 into one csv file containing each unique row between them.
## Youtube Comment Scrape
Given a youtube video id, this will make a .csv of the comments of that video, including replies, (but currently not including comment authors)
# Other Notes
Currently only runs on windows because I haven't got selenium set up cross OS. 
That will be fixed ~soon~

# Terms of Use
Copyright withp; Every piece of java source code in this repository is released under the AGPL V3 with all that that implies.