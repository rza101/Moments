<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        // disini user_id FOLLOWING user_following
        // jadi user_id punya following sebanyak user_following
        // user_following punya followers sebanyak user_id
        Schema::create('user_follow', function (Blueprint $table) {
            $table->id();
            $table->string('username');
            $table->string('username_following');
            $table->foreign('username')->references('username')->on('users')->cascadeOnDelete();
            $table->foreign('username_following')->references('username')->on('users')->cascadeOnDelete();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('user_follow');
    }
};
