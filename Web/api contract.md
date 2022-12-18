# Moments API (JSON)
## User Controller
### Index
Endpoints : /api/users
Method : GET

### Store
Endpoints : /api/users
Method : POST
Parameter :
1. user_id
2. full_name
3. profile_picture

### Show
Endpoints : /api/users/{user_id}
Method : GET

### Update
Endpoints : /api/users/{user_id}
Method : PUT | PATCH
Parameter :
1. full_name
2. profile_picture

### destroy
Endpoints : /api/users/{user_id}
Method : DELETE

## User Follow Controller
### Index
Endpoints : /api/userfollow
Method : GET

### Store
Endpoints : /api/userfollow
Method : POST
Parameter :
1. user_id
2. user_following

### Show
Endpoints : /api/userfollow/{user_id}
Method : GET

### Update
Endpoints : /api/userfollow/{id}
Method : PUT | PATCH

### destroy
Endpoints : /api/userfollow/{id}
Method : DELETE

## Post Controller
### Index
Endpoints : /api/posts
Method : GET

### Store
Endpoints : /api/posts
Method : POST
Parameter :
1. user_id
2. image_url
3. caption

### Show
Endpoints : /api/posts/{id}
Method : GET

### Update
Endpoints : /api/posts/{id}
Method : PUT | PATCH
Parameter :
1. caption

### destroy
Endpoints : /api/posts/{id}
Method : DELETE

## Post Like Controller
### Index
Endpoints : /api/postlikes
Method : GET

### Store
Endpoints : /api/postlikes
Method : POST
Parameter :
1. user_id
2. post_id

### Show
Endpoints : /api/postlikes/{post_id}
Method : GET

### Update
Endpoints : /api/postlikes/{id}
Method : PUT | PATCH

### destroy
Endpoints : /api/postlikes/{id}
Method : DELETE

## Post Comments Controller
### Index
Endpoints : /api/postcomments
Method : GET

### Store
Endpoints : /api/postcomments
Method : POST
Parameter :
1. user_id
2. post_id
3. comment

### Show
Endpoints : /api/postcomments/{post_id}
Method : GET

### Update
Endpoints : /api/postcomments/{id}
Method : PUT | PATCH

### destroy
Endpoints : /api/postcomments/{id}
Method : DELETE

## Saved Posts Controller
### Index
Endpoints : /api/savedposts
Method : GET

### Store
Endpoints : /api/savedposts
Method : POST
Parameter :
1. user_id
2. post_id
3. comment

### Show
Endpoints : /api/savedposts/{user_id}
Method : GET

### Update
Endpoints : /api/savedposts/{id}
Method : PUT | PATCH

### destroy
Endpoints : /api/savedposts/{id}
Method : DELETE