# Android
My Android Work


Simple_Browser_V1 
  Lab 7 for CIS 4350 Mobile Development Temple University
	A simple web browser that takes a string from the user, attempts to fetch the data, and display in WebView
	Widgets
	  EditText – Get User input
	  ImageButton – onClickListener to start AsyncTask to get web data
	  WebView – Display web content
  Relative Layout
    Button and EditText take height 25dp and fill parent width
    WebView gets rest of space
    Border.xml – draws rectangle border around EditText
  Method - String parseForHTTP(String)
	  Checks to see if user input starts with http:// or https://, 
    If not put it at beginning of string
  Private Class GetHTML
	  AsyncTask that gets HTTP Resource and uses InputStream, BufferedReader, and StringBuilder to construct the HTML
	  content for the WebView	

