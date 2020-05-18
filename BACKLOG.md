# Feature
> User login

## Item
> Cash Register App users must log into app to use features. Login is implemented by Cash Register Server.

### Tasks
* Create a branch and switch to it
* Create view for login
* Make controller class and connect it login view
* Send HTTP request to Cash Register Server with username and password
* Acquire JWT for future use and store it for other controllers
* Provide function for handling errors if any
* Commit and push to your branch
* Make pull request

# Feature
> Authorization for different users

## Item
> Provide role-based UI for differenet users. Provide access to must-have features only.

### Tasks
* Create a branch and switch to it
* Make model for role-based UI
* Classify users based on their roles
* Make simple mechanism to render different UI based on a role
* Provide authorization for users so that no one can access non-authorized feature
* Commit and push to your branch
* Make pull request

# Feature
> Search prodcuts

## Item 
> Provide mechanism for searching different products in order to add them to shopping cart

### Tasks
* Create a branch and switch to it
* Make product model
* Update products table based on filters
* Provide mechanism for adding products to shopping cart
* Make view for presenting products in products table
* Connect view to controller
* Commit and push to your branch
* Make pull request

# Feature
> Supplies

## Item
> Show supplies for all available products

### Tasks
* Create a branch and switch to it
* Create view for presenting product supplies to users
* Create controller and connect it to supplies view
* Provide presentation logic for showing supplies
* Make HTTP Request to Cash Register Server to get products
* Store products and use them in controller
* Provide error mechanism for HTTP requests
* Commit and push your branch
* Make pull request

# Feature
> Discount

## Item
> Calculate discount for all products

### Tasks
* Create a branch and switch to it
* Provide a method for calculating discount in Product model
* Provide a method for calculating new price 
* Commit and push your branch
* Make pull request

# Feature
> Filtering products

## Item
> Provide mechanism for filtering products in order to add them to shopping cart

### Tasks
* Create a branch and switch to it
* Create a controller for managing data for filtering
* Connect controller to product table
* Enable two different filtering mechanisms
* Create a view for the controller
* Embedd views for items **3** and **7** into this view
* Commit and push your branch
* Make pull request

# Feature
> Creating receipts

## Item
> Enable main functionality of cash registers - creating receipts

### Tasks
* Create a branch and switch to it
* Create a controller for managing data for receipts
* Connect controller to other controllers responsible for filtering data
* Create a model for receipts
* Create a view for controller
* Provide a mechanism for specifying number of items in receipts
* Provide a mechanism for removing items from receipt
* Commit and push your branch
* Make pull request

# Feature
> Invalidation/canceling receipts

## Item
> Enable functionality for invalidation or canceling receipts.

### Tasks
* Create a branch and switch to it
* Create a controller for presenting all receipts
* Create view and connect it to controller
* Load list of paid receipes
* Make mechanism for choosing a receipt
* Show dialog box for choosing options
* Enable canceling if chosen
* Enable invalidation if chosen and load receipt into cash register
* Update receipt list
* Commit and push your branch
* Make pull request

# Feature
> Payment methods

## Item
> Enable three payment methods for cash register: cash, credit card and by scanning qr code.

### Tasks
* Create a branch and switch to it
* Create a menu for choosing payment method
* Create receipt from receipt table
* Load receipt into payment controller
* Enable payment processing controller
* Inject payment method into controller
* Make request for paying
* Connect to local server if card card payment is required
* Generate QR code if payment by scanning is required
* Poll local server if payment by QR code is chosen
* Wait for answer and show appropriate message
* Empty receipt table and return to cash register main controller
* Commit and push your branch
* Make pull request

# Feature
> Payment

## Item
> Enable paying mechanism for each payment method.

### Tasks
* Create a branch and switch to it
* Get payment method from payment processing controller
* Make mechanism for each payment method
* Connect client to socket if credit card is chosen and enable client-server communication
* Await for server's response
* Process payment based on server's response
* Send HTTP request if payment by cash is chosen
* Await for Cash Register Server's response
* Poll Cash Register Server if payment by QR code is chosen
* Process payment and present answer to customer
* Commit and push your branch
* Make pull request

# Feature
> Loading receipts added by SellerApp

## Item
> Enable mechanism for loading all receipts added by SellerApp so payment can proceed.

### Tasks
* Create a branch and switch to it
* Create option for importing SellerApp receipts
* Make controller and load SellerApp receipts into it
* Make view and connect it to the controller
* Enable search mechanism for receipts
* Enable choosing desired receipt so that it could be loaded into cash register
* Disable add to cart option after SellerApp receipt is loaded
* Inject receipt ID into cash register controller
* Commit and push your branch
* Make pull request

# Feature
> Payment for SellerApp receipts

## Item
> Enable mechanism for payment of desired SellerApp receipt.

### Tasks
* Create a branch and switch to it
* Find imported SellerApp receipt from SellerAppBill controller
* Set its id and populate receipt table
* Disable UI for adding products
* Create receipt from receipt table 
* Inject receipt into Payment controller
* Commit and push your branch
* Make pull request

# Feature
> Cancelling SellerApp receipts

## Item
> Enable mechanism for discarding SellerApp receipts if needed.

### Tasks
* Create a branch and switch to it
* Enable button for cancelling/discarding
* Show dialog for user
* Enable Yes/Cancel option in the dialog box
* If Yes is chosen proceed with cancelling receipt
* Send DELETE HTTP request to cancel the receipt
* Enable GUI again and empty receipt table
* Commit and push your branch
* Make pull request


# Feature
> Providing a receipt to customers

## Item
> Mechanism for generating PDF

### Tasks
* Create a branch and switch to it
* Provide service for generating PDF
* Add listener for generating PDF
* Generate PDF when payment is successful
* Create PDF table for each receipt
* Commit and push your branch
* Make pull request


# Feature
> List of all receipts

## Item
> Feature for information on all receipts

### Tasks
* Create a branch and switch to it
* Create tab for preseting receipts
* Create controller for the tab
* Make HTTP request to get all receipts
* Make list for showing receipts
* Commit and push your branch
* Make pull request


# Feature
> Total amount for receipts on given day/date

## Item
> Show total amount for receipts

### Tasks
* Create a branch and switch to it
* Add date picker to Receipts tab
* Caculate total amount
* Commit and push your branhc
* Make pull request


# Feature
> Offline mode

## Item
> Cash register must work even if cash register server is not available

### Tasks
* Create a branch and switch to it
* Create listener for controllers
* Make service to ping server
* Enable subscription to the service for controllers
* Call offline mode for controllers if server not available
* Call online mode when server becomes available
* Enable offline login
* Store receipts in offline mode and then send them to the server in online mode
* Commit and push your branch
* Make pull request


# Feature
> Lock cash register

## Item
> Enable option for locking cash register

### Tasks
* Create a branch and switch to it
* Enable option for locking
* Make controller for locking
* Ask for password when user wants to unlock
* Return to main tab if password is correct
* Commit and push your branch
* Make pull request


# Feature
>  Generating daily report

## Item
> Generate daily report after cash register is locked (server lock)

### Tasks
* Create a branch and switch to it
* Create option in settings
* Make PDF service and factory
* List all receipts on given day
* Show prices for each receipts
* Commit and push your branch
* Make pull request


# Feature
> Log out

## Item
> Enable option for log out

### Tasks
* Create a branch and switch to it
* Enable option for log out in menu
* Delete current user
* Log out and show login form
* Commit and push your branch
* Make pull request


# Feature
> Calculating tender amount

## Item
> Helping mechanism to calulcate tender amount

### Tasks
* Create a branch and switch to it
* Create calculator when choosing payment method
* Enable simple calculations for users
* Show tender amount on calculator screen
* Commit and push your branch
* Make pull request


# Feature
> Providing a receipt to customers

## Item
> Mechanism for generating PDF

### Tasks
* Create a branch and switch to it
* Provide service for generating PDF
* Add listener for generating PDF
* Generate PDF when payment is successful
* Create PDF table for each receipt
* Commit and push your branch
* Make pull request

# Feature
> Tab for orders

## Item
> Show tab for orders in main menu

### Tasks
* Create a branch and switch to it
* Create controller for orders
* Show orders in grid
* Create controller for editing each order
* Enable editing in controller
* Show lists of products in the order
* Enable deleting order
* Enable paying order
* Commit and push your branch
* Make pull request



