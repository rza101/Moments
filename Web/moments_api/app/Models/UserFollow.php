<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class UserFollow extends Model
{
    use HasFactory;

    protected $table = "user_follow";

    protected $fillable = [
        'username',
        'username_following'
    ];

    function User(){
        return $this->belongsTo(User::class, "username", "username");
    }
}
