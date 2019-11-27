package com.ekino.oss.hibernate

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApplicationTest

fun main(args: Array<String>) {
  runApplication<ApplicationTest>(*args) {
    setBannerMode(Banner.Mode.OFF)
  }
}
