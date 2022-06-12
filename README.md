1. compress 3 file
- key.properties
- mikropos.jks
- google-services.json
2. rename to key_files.zip
3. on terminal run
gpg --symmetric --cipher-algo AES256 key_files.zip
4. move to root project (replace)