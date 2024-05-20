package com.example.balls

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout

class BallAnimationExample : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val container = findViewById<LinearLayout>(R.id.container)
        container.addView(MyAnimationView(this))
    }
}
