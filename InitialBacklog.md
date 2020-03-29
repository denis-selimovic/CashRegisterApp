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
