#!/bin/bash

# Script to verify Java 23 configuration for IntelliJ IDEA and Gradle

echo "=========================================="
echo "Java 23 Configuration Verification"
echo "=========================================="
echo ""

echo "1. JAVA_HOME Environment Variable:"
echo "   JAVA_HOME = $JAVA_HOME"
echo ""

echo "2. Java Version:"
java -version 2>&1
echo ""

echo "3. Gradle java.home setting:"
grep "org.gradle.java.home" gradle.properties
echo ""

echo "4. Gradle Version and JVM:"
cd "/Users/oliverjaramillo/Local Documents/Hyperskill/Cinema Room REST Service with Kotlin"
./gradlew -version 2>&1 | tail -5
echo ""

echo "5. Running Tests to Verify Java 23 Compilation:"
./gradlew test --info 2>&1 | grep -i "java\|jvm\|version" | head -10
echo ""

echo "=========================================="
echo "Configuration Complete!"
echo "=========================================="

