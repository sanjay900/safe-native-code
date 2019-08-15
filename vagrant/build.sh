#!/bin/sh
vagrant up
rm openjdk.box
vagrant package --output openjdk.box
