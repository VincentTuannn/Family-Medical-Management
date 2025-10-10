package Backend.FMM.Controller;

import Backend.FMM.DTO.TransferDTO;
import Backend.FMM.Service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {
    @Autowired
    private TransferService transferService;

    @GetMapping
    public List<TransferDTO> getAllTransfers() {
        return transferService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferDTO> getTransferById(@PathVariable Integer id) {
        Optional<TransferDTO> transfer = transferService.findById(id);
        return transfer.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<TransferDTO> getTransfersByUserId(@PathVariable Integer userId) {
        return transferService.findAllByUserId(userId);
    }

    @PostMapping
    public TransferDTO createTransfer(@RequestBody TransferDTO dto) {
        return transferService.save(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransferDTO> updateTransfer(@PathVariable Integer id, @RequestBody TransferDTO dto) {
        Optional<TransferDTO> existing = transferService.findById(id);
        if (existing.isPresent()) {
            dto.setTransferId(id);
            return ResponseEntity.ok(transferService.save(dto));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransfer(@PathVariable Integer id) {
        transferService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
