#! /bin/sh
# install:
# jq from https://stedolan.github.io/jq/
# gh from https://cli.github.com/manual/tagg
for num in `gh release list 2>/dev/null | awk '{print $1}'`; do
  echo "gh release delete $num -y"
  gh release delete $num -y
done
gh release list | sed 's/|/ /' | awk '{print $1, $8}' | while read -r line; do gh release delete -y "$line"; done
git tag -d $(git tag -l)
git fetch
# Note: pushing once should be faster than multiple times
git push origin --delete $(git tag -l)
git tag -d $(git tag -l)

