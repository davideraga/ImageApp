from xmlrpc.server import SimpleXMLRPCServer
from xmlrpc.server import SimpleXMLRPCRequestHandler

# Restrict to a particular path.
gang="gang"

def hello(name):
    if(gang=="resc"):
        return "helo"
    else:
        return "nope"
    #return gang+'hello from server '+name

# Create server
with SimpleXMLRPCServer(('localhost', 8000)) as server:

    # Register pow() function; this will use the value of
    # pow.__name__ as the name, which is just 'pow'.
    server.register_function(hello, "hello")
    gang="resc"
    print("server is listening")
    # Run the server's main loop
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nKeyboard interrupt received, exiting.")
        sys.exit(0)