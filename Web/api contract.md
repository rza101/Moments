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
2. username
3. full_name
4. fcm_token

### Show
Endpoints : /api/users/{username}
Method : GET

### Update
Endpoints : /api/users/{user_id}/update
Method : POST
Parameter :
1. fcm_token
2. full_name
3. image_url

## User Follow Controller
### Store
Endpoints : /api/userfollow
Method : POST
Parameter :
1. username
2. username_following

### Show
Endpoints : /api/userfollow/{username}
Method : GET

### destroy
Endpoints : /api/userfollow/{id}/delete
Method : GET

## Post Controller
### Index
Endpoints : /api/posts
Method : GET
Parameter :
1. username

### Store
Endpoints : /api/posts
Method : POST
Parameter :
1. username
2. image_url
3. caption

### Show
Endpoints : /api/posts/{id}
Method : GET

### Update
Endpoints : /api/posts/{id}/update
Method : POST
Parameter :
1. caption

### destroy
Endpoints : /api/posts/{id}/delete
Method : GET

## Post Like Controller
### Store
Endpoints : /api/postlikes
Method : POST
Parameter :
1. username
2. post_id

### Show
Endpoints : /api/postlikes/{post_id}
Method : GET

### destroy
Endpoints : /api/postlikes/{id}/delete
Method : GET

## Post Comments Controller
### Store
Endpoints : /api/postcomments
Method : POST
Parameter :
1. username
2. post_id
3. comment

### Show
Endpoints : /api/postcomments/{post_id}
Method : GET

### destroy
Endpoints : /api/postcomments/{id}/delete
Method : GET

## Saved Posts Controller
### Store
Endpoints : /api/savedposts
Method : POST
Parameter :
1. username
2. post_id

### Show
Endpoints : /api/savedposts/{username}
Method : GET

### destroy
Endpoints : /api/savedposts/{id}/delete
Method : GET