环境配置命令
拉取kibana的命令:
docker pull docker.elastic.co/kibana/kibana:8.10.4

在docker中创建网络:
docker network create mynet

将elasticsearch加入网络:
docker network connect mynet <elasticsearch_container_id>

启动kibana:
docker run -d --name kibana -p 5605:5601 --network mynet -e "ELASTICSEARCH_HOSTS=http://es01:9200" -e "XPACK_SECURITY_ENABLED=false" docker.elastic.co/kibana/kibana:8.10.4