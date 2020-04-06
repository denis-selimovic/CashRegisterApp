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




