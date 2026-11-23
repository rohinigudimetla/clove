package com.rohini.clove.cafe;

import com.rohini.clove.queue.handler.JobContext;
import com.rohini.clove.queue.handler.JobHandler;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

@Component
public class ExportHandler implements JobHandler {

    private JsonMapper jsonMapper = new JsonMapper();
    private OrderRepository orderRepository;
    private S3Client s3Client = S3Client.create();
    private String bucket = System.getenv("CLOVE_S3_BUCKET");

    public ExportHandler(OrderRepository repository){
        orderRepository = repository;
    }

    @Override
    public String jobType() {
        return "export";
    }

    @Override
    public String handle(JobContext context) {
        String payload = context.payload();
        JsonNode root = jsonMapper.readTree(payload);
        int rowLimit = root.get("rowLimit").asInt();
        StringBuilder csv = new StringBuilder();
        csv.append("id,order_number,ordered_at,item,quantity,price_cents\n");
        for (int offset = 0; offset < rowLimit; offset = offset + 1000) {
            int size = Math.min(1000, rowLimit - offset);
            List<Order> orders = orderRepository.findChunk(size, offset);
            for (Order order : orders) {
                String line = order.id + "," + order.orderNumber + "," + order.orderedAt + "," + order.item + "," + order.quantity + "," + order.priceCents;
                csv.append(line + "\n");
            }
            int percent = (offset + size) * 100 / rowLimit;
            context.reportProgress(percent);
        }
        String key = "exports/" + UUID.randomUUID() + ".csv";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        RequestBody body = RequestBody.fromString(csv.toString());
        s3Client.putObject(request, body);
        return key;
    }
}
