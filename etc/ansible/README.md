## Install ansible using the python package installer:

```bash
pip3 install -U ansible boto boto3 awscli #last three are needed for aws
```

## Set the authentication details as environment variables:
```bash
export AWS_REGION='<region>'
export AWS_ACCESS_KEY_ID='<key_id>'
export AWS_SECRET_ACCESS_KEY='<secret_key>'
export MYSQL_DB_USERNAME='<username>'
export MYSQL_DB_PASSWORD='<password>'
```

## Run the playbook:
```bash
ansible-playbook deploy-to-aws.yml
```
